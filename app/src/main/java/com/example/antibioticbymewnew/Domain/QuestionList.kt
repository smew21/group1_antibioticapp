package com.example.antibioticbymewnew.Domain

import java.util.ArrayList

/**
 * Created by Bajarng on 4/12/2560.
 */
object QuestionList {

    private val questionlist = ArrayList<Question>()

    fun getQuestion(): ArrayList<Question> {
        return questionlist
    }

}