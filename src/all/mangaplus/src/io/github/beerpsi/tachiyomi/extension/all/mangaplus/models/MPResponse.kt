package io.github.beerpsi.tachiyomi.extension.all.mangaplus.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class MPResponse(
    @ProtoNumber(1) val success: MPSuccessResult? = null,
    @ProtoNumber(2) val error: MPErrorResult? = null,
)

@Serializable
data class MPSuccessResult(
    @ProtoNumber(2) val registrationData: MPRegistrationData? = null,
    @ProtoNumber(8) val titleDetailView: MPTitleDetailView? = null,
    @ProtoNumber(10) val mangaViewer: MPMangaViewer? = null,
    @ProtoNumber(24) val homeViewV3: MPHomeViewV3? = null,
    @ProtoNumber(26) val settingsViewV2: MPSettingsViewV2? = null,
    @ProtoNumber(35) val searchView: MPSearchView? = null,
)

@Serializable
data class MPErrorResult(
    val action: MPErrorAction = MPErrorAction.DEFAULT,
    val englishPopup: MPErrorPopup,
    val popups: List<MPErrorPopup>,
)

@Serializable
data class MPErrorPopup(
    val subject: String,
    val body: String,
    val language: MPLanguage = MPLanguage.ENGLISH,
)

@Serializable
enum class MPErrorAction {
    DEFAULT,
    UNAUTHORIZED,
    MAINTENANCE,
    GEOIP_BLOCKING,
}
