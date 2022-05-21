package com.jeffreytht.gobblet.ui

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
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.adapter.PeacesAdapter
import com.jeffreytht.gobblet.databinding.GameActivityBinding
import com.jeffreytht.gobblet.model.*
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.util.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GameActivityViewModel(
    private val gameSetting: GameSetting,
    private val soundUtil: SoundUtil,
    private val resourcesProvider: ResourcesProvider,
    private val dialogBuilder: DialogBuilder,
    private val aiPlayer: AIPlayer,
    private val navigator: Navigator,
    private val adUtil: AdUtil,
    private val adsCallback: AdsCallback
) : ViewModel(), PeaceHandler {
    companion object {
        const val LINE_COLOR_DELAY = 300L
    }

    private val disposable = CompositeDisposable()
    private val gridAdapter: GridAdapter
    private val peacesAdapterMap = HashMap<@Peace.Color Int, PeacesAdapter>()
    private val game: Game = Game(gameSetting)
    val observableTitle = ObservableField<String>()
    val observableTitleColor = ObservableField(R.color.white)

    init {
        gridAdapter = GridAdapter(game.grids, this, resourcesProvider)
        for ((color, peaces) in game.peaces) {
            peacesAdapterMap[color] = PeacesAdapter(peaces, this, resourcesProvider)
        }
        newGame()
        game.registerGameInteractor(gridAdapter)
        game.registerGameInteractor(peacesAdapterMap.values)
    }

    private fun newGame() {
        game.reset()
        gridAdapter.resetData()
        for ((_, adapter) in peacesAdapterMap) {
            adapter.resetData()
        }
        adsCallback.showAds(adUtil)
    }

    fun onNewGameClicked() {
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
                            newGame()
                        }
                    }
                } else {
                    newGame()
                }
            }
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
                            when (it) {
                                GREEN -> resourcesProvider.getString(R.string.green)
                                else -> resourcesProvider.getString(R.string.red)
                            }
                        )
                    )
                }
                .filter { gameSetting.mode == Game.SINGLE_PLAYER && it == aiPlayer.aiColor }
                .observeOn(Schedulers.computation())
                .flatMapSingle { aiPlayer.getNextMove(game.grids, game.peaces) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (peace, grid) ->
                    soundUtil.play(Sound.CLICK)
                    game.move(peace, grid)
                },
            game
                .getWinnerObservable()
                .distinctUntilChanged()
                .filter { it != Winner.NO_WINNER }
                .flatMapCompletable { winner ->
                    binding.textViewNewGame.isClickable = false
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

                    Observable.fromIterable(lines)
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
                            binding.textViewNewGame.isClickable = true
                        }
                        .toList()
                        .ignoreElement()
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
        game
            .getWinnerObservable()
            .firstElement()
            .filter { it == Winner.NO_WINNER }
            .flatMap { game.getPlayerTurnObservable().firstElement() }
            .filter { !(gameSetting.mode == Game.SINGLE_PLAYER && it == aiPlayer.aiColor) }
            .map {
                if (it != peace.color) {
                    resourcesProvider.makeToast(
                        resourcesProvider.getString(R.string.not_your_turn),
                        Toast.LENGTH_SHORT
                    )
                }
                it == peace.color
            }
            .filter { it }
            .subscribe {
                val myShadow = resourcesProvider.getDrawable(peace.resId)?.let {
                    PeaceShadow(it, imageView)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageView.startDragAndDrop(null, myShadow, peace, 0)
                } else {
                    imageView.startDrag(null, myShadow, peace, 0)
                }
            }
        return true
    }

    override fun onDropToGrid(peace: Peace, grid: Grid) {
        if (game.move(peace, grid)) {
            soundUtil.play(Sound.CLICK)
        }
    }

    fun onBackPressed() {
        soundUtil.play(Sound.CLICK)
        adsCallback.showAds(adUtil)
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
                            navigator.finish()
                        }
                    }
                } else {
                    navigator.finish()
                }
            }
    }

    fun onDestroy() {
        disposable.dispose()
    }
}

