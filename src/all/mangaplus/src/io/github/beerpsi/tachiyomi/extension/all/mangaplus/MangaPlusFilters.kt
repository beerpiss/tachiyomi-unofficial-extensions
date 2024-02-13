package io.github.beerpsi.tachiyomi.extension.all.mangaplus

import eu.kanade.tachiyomi.lib.i18n.Intl
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import io.github.beerpsi.tachiyomi.extension.all.mangaplus.models.MPAllTitlesGroup
import io.github.beerpsi.tachiyomi.extension.all.mangaplus.models.MPLabel
import io.github.beerpsi.tachiyomi.extension.all.mangaplus.models.MPLabelCode
import io.github.beerpsi.tachiyomi.extension.all.mangaplus.models.MPLanguage
import io.github.beerpsi.tachiyomi.extension.all.mangaplus.models.MPTitle

object MangaPlusFilters {
    fun getFilterList(intl: Intl): FilterList = FilterList(
        AuthorFilter(intl),
        LabelList(intl["serialization_filter"], getSerializationList(intl)),
        TagList(intl["genre"], getGenreList(intl)),
        SortFilter(intl),
    )

    fun filterMangaList(
        titles: List<MPAllTitlesGroup>,
        language: MPLanguage,
        query: String,
        filters: FilterList,
    ): List<MPTitle> {
        val filtered = titles.toMutableList()

        if (query.isNotEmpty()) {
            filtered.retainAll { tg ->
                tg.titles.any { it.name.contains(query, true) }
            }
        }

        filters.forEach { f ->
            when (f) {
                is AuthorFilter -> if (f.state.isNotEmpty()) {
                    filtered.retainAll { tg ->
                        tg.titles.any { it.author.contains(f.state, true) }
                    }
                }
                is TagList -> {
                    val included = mutableListOf<String>()
                    val excluded = mutableListOf<String>()

                    f.state.forEach {
                        when (it.state) {
                            Filter.TriState.STATE_INCLUDE -> included.add(it.slug)
                            Filter.TriState.STATE_EXCLUDE -> excluded.add(it.slug)
                            else -> {}
                        }
                    }

                    if (included.isNotEmpty()) {
                        filtered.retainAll { tg ->
                            tg.tags.any { included.contains(it.slug) }
                        }
                    }

                    if (excluded.isNotEmpty()) {
                        filtered.removeAll { tg ->
                            tg.tags.any { excluded.contains(it.slug) }
                        }
                    }
                }
                is LabelList -> {
                    val included = f.state.filter { it.state }.map { it.label }

                    filtered.retainAll { tg ->
                        tg.label?.label?.let {
                            included.contains(it)
                        } ?: false
                    }
                }
                else -> {}
            }
        }

        filters.filterIsInstance<SortFilter>().firstOrNull()?.state?.let { s ->
            when (s.index) {
                0 -> filtered.sortBy { it.theTitle }
                1 -> filtered.sortBy { it.nextChapterStartTimestamp }
                else -> throw IllegalStateException("Unknown sort filter index ${s.index}")
            }

            if (!s.ascending) {
                filtered.reverse()
            }
        }

        return filtered.flatMap { tg ->
            tg.titles.filter { it.language == language }
        }
    }

    private fun getGenreList(intl: Intl) = listOf(
        Tag(intl["battle_action"], "battle-action"),
        Tag(intl["comedy"], "comedy"),
        Tag(intl["sport_club_activities"], "sport"),
        Tag(intl["mystery_thriller"], "mystery-thriller"),
        Tag(intl["romance"], "romance"),
        Tag(intl["horror_supernatural"], "horror-supernatural"),
        Tag(intl["spin_off"], "spin-off"),
        Tag(intl["sci_fi_fantasy"], "sci-fi-fantasy"),
        Tag(intl["romantic_comedy"], "romantic-comedy"),
        Tag(intl["drama"], "drama"),
        Tag(intl["food"], "food"),
        Tag(intl["history_period"], "history-period"),
        Tag(intl["gangsta"], "gangsta"),
        Tag(intl["one_shot"], "one-shot"),
        Tag(intl["manga_award"], "manga-award"),
    )

    private fun getSerializationList(intl: Intl) = enumValues<MPLabelCode>().map {
        val name = when (it) {
            MPLabelCode.REVIVAL -> intl["revival"]
            MPLabelCode.OTHERS -> intl["other"]
            else -> MPLabel(it).magazine!!
        }

        Label(name, it)
    }

    private class AuthorFilter(intl: Intl) : Filter.Text(intl["author"])

    private class Tag(name: String, val slug: String) : Filter.TriState(name)

    private class TagList(name: String, tags: List<Tag>) : Filter.Group<Tag>(name, tags)

    private class Label(name: String, val label: MPLabelCode) : Filter.CheckBox(name)

    private class LabelList(name: String, labels: List<Label>) : Filter.Group<Label>(name, labels)

    private class SortFilter(intl: Intl) : Filter.Sort(
        intl["sort"],
        arrayOf(
            intl["sort_title"],
            intl["sort_latest_update"],
        ),
        Selection(1, false),
    )
}
