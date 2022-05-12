package com.jeffreytht.gobblet.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.ActivityGobbletBinding
import com.jeffreytht.gobblet.di.DaggerGameComponent
import com.jeffreytht.gobblet.di.DaggerGridAdapterComponent
import com.jeffreytht.gobblet.di.DaggerPeaceAdapterComponent
import com.jeffreytht.gobblet.model.*
import com.jeffreytht.gobblet.model.GobbletMode.Companion.SINGLE_PLAYER
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GobbletActivityViewModel(
    private val dimension: Int,
    context: Context,
    @GobbletMode.Mode private val gobbletMode: Int,
    private val resourcesProvider: ResourcesProvider,
    private val dialogBuilder: DialogBuilder,
    private val aiPlayer: AIPlayer
) : ViewModel(), PeaceHandler {
    private val disposable = CompositeDisposable()
    var observableTitle = ObservableField<String>()
    var observableTitleColor = ObservableField<@ColorRes Int>(R.color.white)

    companion object {
        const val LINE_COLOR_DELAY = 300L
    }

    private val game = DaggerGameComponent
        .builder()
        .withDimension(dimension)
        .build()
        .provideGame()

    private val gridAdapter = DaggerGridAdapterComponent
        .builder()
        .withPeaceHandler(this)
        .withContext(context)
        .withData(game.grids)
        .build()
        .providesGridAdapter()

    private val peacesAdapterMap = hashMapOf(
        GREEN to DaggerPeaceAdapterComponent
            .builder()
            .withContext(context)
            .withData(game.peacesMap[GREEN]!!)
            .withPeaceHandler(this)
            .build()
            .providesPeacesAdapter(),

        RED to DaggerPeaceAdapterComponent
            .builder()
            .withContext(context)
            .withData(game.peacesMap[RED]!!)
            .withPeaceHandler(this)
            .build()
            .providesPeacesAdapter(),
    )

    init {
        game.registerGameInteractor(gridAdapter)
        game.registerGameInteractor(peacesAdapterMap.values)
    }

    fun init(binding: ActivityGobbletBinding, context: Context) {
        binding.vm = this
        binding.adViewGobblet.loadAd(AdRequest.Builder().build())
        binding.gobbletRecyclerView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            dimension.toFloat()
        )
        initializePeaces(binding.recyclerViewRedPeaces, RED, context)
        initializePeaces(binding.recyclerViewGreenPeaces, GREEN, context)
        initializeGrids(binding.gobbletRecyclerView, context)
        disposable.addAll(
            game
                .getPlayerTurnObservable()
                .subscribe {
                    observableTitleColor.set(R.color.white)
                    observableTitle.set(
                        resourcesProvider.getString(
                            R.string.player_turn,
                            resourcesProvider.getString(if (it == GREEN) R.string.green else R.string.red)
                        )
                    )
                },
            game.getPlayerTurnObservable()
                .filter { gobbletMode == SINGLE_PLAYER && it == aiPlayer.aiColor }
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
                        for (i in 0 until dimension) {
                            for (j in 0 until dimension) {
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
                            for (i in 0 until game.dimension) {
                                lines.add(Pair(winner.idx, i))
                            }
                        }
                        Winner.COL -> {
                            for (i in 0 until game.dimension) {
                                lines.add(Pair(i, winner.idx))
                            }
                        }
                        Winner.LEFT_DIAGONAL -> {
                            for (i in 0 until game.dimension) {
                                lines.add(Pair(i, i))
                            }
                        }
                        Winner.RIGHT_DIAGONAL -> {
                            for (i in 0 until game.dimension) {
                                lines.add(Pair(i, game.dimension - 1 - i))
                            }
                        }
                    }

                    return@flatMapCompletable Observable.fromIterable(lines)
                        .concatMap {
                            Observable.just(it).delay(LINE_COLOR_DELAY, TimeUnit.MILLISECONDS)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext {
                            gridAdapter.setGridBackground(
                                R.drawable.bg_grid_colored,
                                it.first,
                                it.second
                            )
                        }
                        .doOnComplete {
                            peacesAdapterMap[GREEN]?.updateImageRes(
                                if (winner.color == GREEN) {
                                    R.drawable.ic_green_large_smile_peace
                                } else {
                                    R.drawable.ic_green_large_sad_peace
                                }
                            )
                            peacesAdapterMap[RED]?.updateImageRes(
                                if (winner.color == RED) {
                                    R.drawable.ic_red_large_smile_peace
                                } else {
                                    R.drawable.ic_red_large_sad_peace
                                }
                            )
                            gridAdapter.setTopPeaceDrawable(
                                RED,
                                if (winner.color == RED) {
                                    R.drawable.ic_red_large_smile_peace
                                } else {
                                    R.drawable.ic_red_large_sad_peace
                                }
                            )
                            gridAdapter.setTopPeaceDrawable(
                                GREEN,
                                if (winner.color == GREEN) {
                                    R.drawable.ic_green_large_smile_peace
                                } else {
                                    R.drawable.ic_green_large_sad_peace
                                }
                            )
                            resourcesProvider.getString(
                                R.string.player_win,
                                resourcesProvider.getString(
                                    if (winner.color == GREEN) {
                                        R.string.green
                                    } else {
                                        R.string.red
                                    }
                                )
                            ).let {
                                resourcesProvider.makeToast(it, Toast.LENGTH_SHORT)
                                observableTitleColor.set(R.color.yellow)
                                observableTitle.set(it)
                            }
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
            object : GridLayoutManager(context, game.dimension) {
                override fun canScrollHorizontally(): Boolean = false
                override fun canScrollVertically(): Boolean = false
            }
    }

    private fun initializePeaces(
        recyclerView: RecyclerView,
        @Peace.Color color: Int,
        context: Context
    ) {
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
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
                    if (gobbletMode == SINGLE_PLAYER && it == aiPlayer.aiColor) {
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
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            activity.finish()
                        }
                    }
                } else {
                    activity.finish()
                }
            }
    }
}

