package uz.creater.iboralarlugati.models

data class WordDic(
    var name: String? = null,
    var translation: String? = null,
    var image: String? = null,
    var categoryId: String? = null,
    @field:JvmField
    var wordGood: Boolean = false
)