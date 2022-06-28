package com.j2k.botAutoCreate.json

import com.google.gson.Gson
import com.j2k.botAutoCreate.exceptions.StepNotFoundException
import com.j2k.botAutoCreate.pathToScriptFile
import com.j2k.botAutoCreate.step.StepBuilder

data class StepsData(
    var id: Long,
    var reason: String,
    var steps: MutableList<StepsData>,
    var text: String
) {
    fun searchNodeById(id: Long): StepsData {
        if (this.id == id) return this

        var result: StepsData? = null
        steps.forEach {
            if (it.id == id) return it
            result = it.searchNodeById(id)
        }

        return result ?: throw StepNotFoundException("Step with id \"$id\" not found ")
    }

    fun searchByChildId(id: Long): StepsData? {
        if (this.id == id) return null

        var result: StepsData? = null
        steps.forEach {
            if (it.id == id) return this
            result = it.searchByChildId(id)
        }

        return result
    }

    fun getMaxId(): Long {
        var maxId = id

        steps.forEach {
            val childMaxId = it.getMaxId()
            if (childMaxId > maxId) maxId = childMaxId
        }

        return maxId
    }
}

object ScriptManager {
    private val path = pathToScriptFile
    private val gson = Gson()
    private val dataObject: StepsData = gson.fromJson(path.toFile().readText(), StepsData::class.java)

    var builder: StepBuilder = StepBuilder.loadSettingsFromData(dataObject, StepBuilder())
        private set

    private fun updateBuilder() {
        builder = StepBuilder.loadSettingsFromData(dataObject, StepBuilder())
    }

    fun deleteStep(id: Long): Long { // return parent.id
        val previousStep = dataObject.searchByChildId(id)!!
        previousStep.steps.removeIf { it.id == id }

        updateBuilder()

        return previousStep.id
    }

    fun editStep(id: Long, text: String) {
        dataObject.searchNodeById(id).text = text
        updateBuilder()
    }

    fun addStep(id: Long, reason: String, text: String): Long {
        val newId = dataObject.getMaxId()
        dataObject.searchNodeById(id).steps.add(
            StepsData(
                newId, reason, mutableListOf(), text
            )
        )
        return newId
    }
}