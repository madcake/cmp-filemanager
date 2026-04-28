package ru.madcake.filemanager.core.domain

data class Branch(
    val meta: BranchMeta?,
    val parentMeta: BranchMeta?,
    val subBranchMetas: List<BranchMeta>,
    val dataObjects: List<DataObjectMeta>,
)