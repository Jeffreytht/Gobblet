package com.jeffreytht.gobblet.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.jeffreytht.gobblet.MyApp
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.adapter.PeacesAdapter
import com.jeffreytht.gobblet.databinding.GameActivityBinding
import com.jeffreytht.gobblet.di.AppDependencies
import com.jeffreytht.gobblet.model.*
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.util.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GameActivityViewModel(
    context: Context,
    private val gameSetting: GameSetting,
    private val soundUtil: SoundUtil,
    private val resourcesProvider: ResourcesProvider,
    private val dialogBuilder: DialogBuilder,
    private val aiPlayer: AIPlayer,
) : ViewModel(), PeaceHandler {
    companion object {
        const val LINE_COLOR_DELAY = 300L
    }

    private val disposable = CompositeDisposable()
    private val peacesAdapterMap = HashMap<@Peace.Color Int, PeacesAdapter>()
    private val game: Game = Game(gameSetting)
    private val gridAdapter: GridAdapter

    var observableTitle = ObservableField<String>()
    var observableTitleColor = ObservableField(R.color.white)

    init {
        val dependencies =
            ((context as Activity).application as MyApp).extractDependency(AppDependencies::class)
                ?: throw Exception()

        gridAdapter = GridAdapter(game.grids, this, dependencies.providesResourcesProvider())

        for ((color, peaces) in game.peaces) {
            peacesAdapterMap[color] = PeacesAdapter(
                peaces,
                this,
                dependencies.providesResourcesProvider()
            )
        }

        game.registerGameInteractor(gridAdapter)
        game.registerGameInteractor(peacesAdapterMap.values)
    }

    fun initView(binding: GameActivityBinding, context: Context) {
        binding.vm = this
        binding.adViewGobblet.loadAd(AdRequest.Builder().build())
        binding.gobbletRecyclerView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            gameSetting.dimension.toFloat()
        )
        initializePeaces(binding.recyclerViewRedPeaces, RED, context)
        initializePeaces(binding.recyclerViewGreenPeaces, GREEN, context)
        initializeGrids(binding.gobbletRecyclerView, context)
        disposable.addAll(
            game
                .getPlayerTurnObservable()
                .doOnNext {
                    observableTitleColor.set(R.color.white)
                    observableTitle.set(
                        resourcesProvider.getString(
                            R.string.player_turn,
                            resourcesProvider.getString(if (it == GREEN) R.string.green else R.string.red)
                        )
                    )
                }
                .skip(1)
                .subscribe {
                    soundUtil.play(Sound.CLICK)
                },
            Observable.combineLatest(
                game.getPlayerTurnObservable(),
                game.getWinnerObservable()
            ) { o1, o2 -> Pair(o1, o2) }
                .filter {
                    it.second == Winner.NO_WINNER
                            && gameSetting.mode == Game.SINGLE_PLAYER
                            && it.first == aiPlayer.aiColor
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val grids = ArrayList<ArrayList<Grid>>()
                    for (i in 0 until game.grids.size) {
                        grids.add(ArrayList())
                        for (j in 0 until game.grids.size) {
                            grids[i].add(game.grids[i][j].deepCopy())
                        }
                    }
                    val peaces = HashMap<@Peace.Color Int, ArrayList<Peace>>()
                    for (elem in game.peaces) {
                        peaces[elem.key] = ArrayList()
                        for (peace in elem.value) {
                            peaces[elem.key]?.add(peace.copy())
                        }
                    }
                    Pair(grids, peaces)
                }
                .observeOn(Schedulers.computation())
                .flatMapSingle {
                    aiPlayer.getNextMove(it.first, it.second)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (peace, grid) ->
                    var targetPeace = game.peaces[aiPlayer.aiColor]?.find { it.id == peace.id }
                    if (targetPeace == null) {
                        for (i in 0 until gameSetting.dimension) {
                            for (j in 0 until gameSetting.dimension) {
                                if (game.grids[i][j].peaces.isNotEmpty()
                                    && game.grids[i][j].peaces.peek().color == peace.color
                                    && game.grids[i][j].peaces.peek().id == peace.id
                                ) {
                                    targetPeace = game.grids[i][j].peaces.peek()
                                }
                            }
                        }
                    }
                    game.move(targetPeace!!, game.grids[grid.row][grid.col])
                },
            game
                .getWinnerObservable()
                .distinctUntilChanged()
                .flatMapCompletable { winner ->
                    if (winner == Winner.NO_WINNER) {
                        return@flatMapCompletable Completable.complete()
                    }

                    val lines = ArrayList<Pair<Int, Int>>()
                    when (winner.line) {
                        Winner.ROW -> {
                            for (i in 0 until gameSetting.dimension) {
                                lines.add(Pair(winner.idx, i))
                            }
                        }
                        Winner.COL -> {
                            for (i in 0 until gameSetting.dimension) {
                                lines.add(Pair(i, winner.idx))
                            }
                        }
                        Winner.LEFT_DIAGONAL -> {
                            for (i in 0 until gameSetting.dimension) {
                                lines.add(Pair(i, i))
                            }
                        }
                        Winner.RIGHT_DIAGONAL -> {
                            for (i in 0 until gameSetting.dimension) {
                                lines.add(Pair(i, gameSetting.dimension - 1 - i))
                            }
                        }
                    }

                    return@flatMapCompletable Observable.fromIterable(lines)
                        .concatMap {
                            Observable.just(it).delay(LINE_COLOR_DELAY, TimeUnit.MILLISECONDS)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext {
                            soundUtil.play(Sound.GAME_COLOR_GRID)
                            gridAdapter.setGridBackground(
                                R.drawable.bg_grid_colored,
                                it.first,
                                it.second
                            )
                        }
                        .doOnComplete {
                            val winnerMessage: String
                            val greenDrawable: Int
                            val redDrawable: Int
                            val sound: Int

                            if (winner.color == GREEN) {
                                sound = Sound.GAME_WIN
                                greenDrawable = R.drawable.ic_green_large_smile_peace
                                redDrawable = R.drawable.ic_red_large_sad_peace
                                winnerMessage = resourcesProvider.getString(
                                    R.string.player_win,
                                    resourcesProvider.getString(
                                        R.string.green
                                    )
                                )
                            } else {
                                sound = if (gameSetting.mode == Game.SINGLE_PLAYER) {
                                    Sound.GAME_LOSE
                                } else {
                                    Sound.GAME_WIN
                                }
                                greenDrawable = R.drawable.ic_green_large_sad_peace
                                redDrawable = R.drawable.ic_red_large_smile_peace
                                winnerMessage = resourcesProvider.getString(
                                    R.string.player_win,
                                    resourcesProvider.getString(
                                        R.string.red
                                    )
                                )
                            }
                            soundUtil.play(sound)
                            peacesAdapterMap[GREEN]?.updateImageRes(
                                greenDrawable
                            )
                            peacesAdapterMap[RED]?.updateImageRes(
                                redDrawable
                            )
                            gridAdapter.setTopPeaceDrawable(
                                RED,
                                redDrawable
                            )
                            gridAdapter.setTopPeaceDrawable(
                                GREEN,
                                greenDrawable
                            )
                            resourcesProvider.makeToast(winnerMessage, Toast.LENGTH_SHORT)
                            observableTitleColor.set(R.color.yellow)
                            observableTitle.set(winnerMessage)
                        }.toList().ignoreElement()
                }.subscribe()

        )
    }

    private fun initializeGrids(
        recyclerView: RecyclerView,
        context: Context
    ) {
        recyclerView.adapter = gridAdapter
        recyclerView.layoutManager =
            object : GridLayoutManager(context, gameSetting.dimension) {
                override fun canScrollHorizontally(): Boolean = false
                override fun canScrollVertically(): Boolean = false
            }
    }

    private fun initializePeaces(
        recyclerView: RecyclerView,
        @Peace.Color color: Int,
        context: Context
    ) {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = peacesAdapterMap[color]
    }

    override fun onLongClick(peace: Peace, imageView: ImageView): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Observable.combineLatest(
                game.getPlayerTurnObservable(),
                game.getWinnerObservable()
            ) { playerTurn, winner -> Pair(playerTurn, winner) }
                .firstElement()
                .filter { it.second == Winner.NO_WINNER }
                .map { it.first }
                .subscribe { it ->
                    if (gameSetting.mode == Game.SINGLE_PLAYER && it == aiPlayer.aiColor) {
                        return@subscribe
                    }

                    if (it != peace.color) {
                        resourcesProvider.makeToast(
                            resourcesProvider.getString(R.string.not_your_turn),
                            Toast.LENGTH_SHORT
                        )
                        return@subscribe
                    }
                    val myShadow = resourcesProvider.getDrawable(peace.resId)?.let {
                        PeaceShadow(it, imageView)
                    }
                    imageView.startDragAndDrop(null, myShadow, peace, 0)
                }
        }
        return true
    }

    override fun onDropToGrid(peace: Peace, grid: Grid) {
        game.move(peace, grid)
    }

    fun onBackPressed(activity: Activity) {
        soundUtil.play(Sound.CLICK)
        game
            .getWinnerObservable()
            .firstElement()
            .subscribe {
                if (it == Winner.NO_WINNER) {
                    dialogBuilder.showDialog(
                        R.string.quit_game_title,
                        R.string.quit_game_message,
                        R.drawable.ic_green_large_peace,
                        R.string.yes,
                        R.string.no,
                    ) { _: DialogInterface, button: Int ->
                        soundUtil.play(Sound.CLICK)
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            activity.finish()
                        }
                    }
                } else {
                    activity.finish()
                }
            }
    }

    fun onDestroy() {
        disposable.dispose()
    }
}

