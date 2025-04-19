package com.example.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class FindUntranslatedStringsTask : DefaultTask() {
    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, PROJECT_DIR_NAME)
        val valuesDirs = resDir.listFiles { file ->
            file.isDirectory && file.name.startsWith(DEFAULT_DIR_NAME)
        }
        var defaultStringResList: List<String> = listOf()
        val missingStrings: MutableMap<String, List<String>> = mutableMapOf()

        valuesDirs?.forEach { dir ->
            val strings = File(resDir, dir.name + FILE_NAME)
            val stringsFromXml = parseStringResFileToNodeList(strings)
            val stringIdentities = parseNamesStringRes(stringsFromXml)
            if (dir.name == DEFAULT_DIR_NAME) {
                defaultStringResList = stringIdentities
            } else {
                missingStrings[dir.name] = findDiffList(defaultStringResList, stringIdentities)
            }
        }
        handleError(missingStrings)
    }

    private fun parseNamesStringRes(stringsFromXml: NodeList): List<String> {
        return stringsFromXml.let { nodeList ->
            (0 until nodeList.length).map { i ->
                val node = nodeList.item(i)
                val name = node.attributes?.getNamedItem(NODE_ATTRIBUTES)?.nodeValue
                    ?: ""
                name
            }
        }
    }

    private fun parseStringResFileToNodeList(stringFile: File): NodeList {
        return DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(stringFile)
            .getElementsByTagName(STRING_TAG_NAME)
    }

    private fun findDiffList(
        defaultList: List<String>,
        applicantList: List<String>,
    ): List<String> {
        val missingStringsList = mutableListOf<String>()
        defaultList.forEach { itemString ->
            if (!applicantList.contains(itemString)) missingStringsList.add(itemString)
        }
        return missingStringsList
    }

    private fun handleError(missingStrings: Map<String, List<String>>) {
        if (missingStrings.isNotEmpty()) {
            val stringBuilderErrorText =
                StringBuilder(ERROR_TITLE).append(System.lineSeparator())
            missingStrings.forEach { key, value ->
                stringBuilderErrorText
                    .append("$SEPARATOR_STRING $key $SEPARATOR_STRING")
                    .append(System.lineSeparator())
                    .append(value.joinToString(separator = System.lineSeparator()))
                    .append(System.lineSeparator())

            }
            throw GradleException(stringBuilderErrorText.toString())
        }
    }

    companion object {
        private const val SEPARATOR_STRING = "==="
        private const val DEFAULT_DIR_NAME = "values"
        private const val PROJECT_DIR_NAME = "src/main/res"
        private const val FILE_NAME = "/strings.xml"
        private const val NODE_ATTRIBUTES = "name"
        private const val STRING_TAG_NAME = "string"
        private const val ERROR_TITLE = "Missing translations"
    }
}