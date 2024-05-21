require: dateTime/moment.min.js
    module = sys.zb-common

require: ./data/diary_content.yaml
    var = diary_contents


theme: /Journal
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Journal/Start", onlyThisState = false
        
        if: $request.query == "Да"
            script:
                $client.diaryExplanationDone = 1;
            timeout: /Journal/DiarySession/Thought || interval = "2 seconds"
                
        elseif: $request.query == "Нет"
            a: {{diary_contents.diary_later}}
            script:
                if (!$client.diaryExplanationDone) {
                    $client.diaryExplanationDone = 0;
                }
            go!: /Start/CommandDescription
        
        elseif: $request.query == "Вернуться в меню"
            go!: /Start
        
        elseif: $request.query == "to_explanation"
            go!: /Journal/Start/Explanation
            
        elseif: $request.query == "to_goal"
            go!: /Journal/Start/DiaryGoal
            
        elseif: $request.query == "to_instructions"
            go!: /Journal/Start/DiaryInstructions
            
        elseif: $request.query == "to_agreement"
            go!: /Journal/Start/Agreement
            
            
    state: CallBackProcessorNo
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/Thought", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/Autothought", onlyThisState = true
        if: $request.query == "Такой ситуации нет" && $context.session.lastState == "/Journal/DiarySession/Thought"
            go!: /Journal/DiarySession/NoSituation
        if: $request.query == "Таких мыслей нет" && $context.session.lastState == "/Journal/DiarySession/Autothought"
            go!: /Journal/DiarySession/NoThought
    
    state: CallBackProcessorEmotion
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/Emotion", onlyThisState = true
        go!: /Journal/DiarySession/EmotionIntensivity
    
    state: CallBackProcessorEmotionIntensivity
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/EmotionIntensivity", onlyThisState = true
        script:
            $session.emotion_intensivity_before = $request.query
            log("$session.emotion_intensivity_before")
            log($session.emotion_intensivity_before)
        go!: /Journal/DiarySession/Autothought
    
    state: CallBackProcessorDistortion
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/DistortionFormulation", onlyThisState = true
        script:
            $session.distortion = $request.query
            log("$session.distortion")
            log($session.distortion)
        go!: /Journal/DiarySession/RationalResponse
    
    state: CallBackProcessorEmotionIntensivityAfter
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/FinalEmotionIntensivity", onlyThisState = true
        script:
            $session.emotion_intensivity_after = $request.query
            log("$session.emotion_intensivity_after")
            log($session.emotion_intensivity_after)
        go!: /Journal/DiarySession/End
    
    state: CallBackProcessorConclusion
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/End/Conclusion", onlyThisState = true
        if: $request.query == "to_journal_writing"
            go!: /Journal/DiarySession/Beginning
        elseif: $request.query == "to_menu"
            go!: /Start
        elseif: $request.query == "/reframe"
            go!: /Consultation/Start
        elseif: $request.query == "to_history"
            go!: /History/HistoryFull
        elseif: $request.query == "to_statistics"
            go!: /Consultation/Start
        
    state: CallBackProcessorwHistory
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/Beginning", onlyThisState = true
        if: $request.query == "show_history"
            go!: /History/HistoryFull
        elseif: $request.query == "add_note"
            go!: /Journal/DiarySession/Thought
    
    state: Start
        q!: $regex</journal>
        q!: $regex</diary>
        if: $client.diaryExplanationDone == 1
            go!: /Journal/DiarySession/Beginning
            
        a: {{diary_contents.diary_session_beg}}\n\n{{diary_contents.diary_begin}}
        inlineButtons:
            { text: "Далее", callback_data: "to_explanation" }
    
        state: Explanation
            a: {{diary_contents.diary_automatic_thoughts}}
            inlineButtons:
                { text: "Далее", callback_data: "to_goal" }
        
        state: DiaryGoal
            a: {{diary_contents.diary_goal}}
            inlineButtons:
                { text: "Далее", callback_data: "to_instructions" }
        
        state: DiaryInstructions
            a: {{diary_contents.diary_instructions}}
            inlineButtons:
                { text: "Далее", callback_data: "to_agreement" }
        
        state: Agreement
            a: {{diary_contents.diary_readiness}}
            script:
                sendInlineButtons($context, ["Да", "Нет"])
    
    state: DiarySession
        
        state: Beginning
            a: {{diary_contents.diary_session_beg}}
            inlineButtons:
                { text: "Добавить запись", callback_data: "add_note" }
                { text: "Посмотреть историю", callback_data: "show_history" }
                { text: "Посмотреть статистику", callback_data: "show_statistics" }
            # timeout: /Journal/DiarySession/Thought || interval = "2 seconds"
        
        state: Thought
            q:* || fromState = "/Journal/DiarySession/NoThought"
            q: Да || fromState = "/Journal/Start/Agreement"
            a: {{diary_contents.diary_situation}}
            script:
                sendInlineButtons($context, ["Такой ситуации нет"])
        
        state: NoSituation
            intent: /нет || fromState = "/Journal/DiarySession/Thought"
            a: {{diary_contents.diary_no_situation}}
            script:
                sendInlineButtons($context, ["Вернуться в меню"])
        
        state: Emotion
            q:* || fromState = "/Journal/DiarySession/Thought"
            q:* || fromState = "/Journal/DiarySession/NoSituation"
            a: {{diary_contents.diary_emotion}}
            script:
                $session.thought = $request.query;
                log("$session.thought")
                log($session.thought)
                sendInlineButtons($context, emotions)
        
        state: EmotionIntensivity
            q:* || fromState = "/Journal/DiarySession/Emotion"
            a: {{diary_contents.diary_emotion_intensivity}}
            script:
                $session.emotion = $request.query
                log("$session.emotion")
                log($session.emotion)
                sendInlineButtons($context, [1, 2, 3, 4, 5])
                sendInlineButtons($context, [6, 7, 8, 9, 10])
        
        state: Autothought
            q:* || fromState = "/Journal/DiarySession/EmotionIntensivity"
            script:
                $session.emotion_intensivity_before = $request.query
                log("$session.emotion_intensivity_before")
                log($session.emotion_intensivity_before)
            a: {{diary_contents.diary_autothought}}
            script:
                sendInlineButtons($context, ["Таких мыслей нет"])
        
        state: NoThought
            intent: /нет || fromState = "/Journal/DiarySession/Autothought"
            a: {{diary_contents.diary_no_thoughts_head_empty}}
            timeout:  /Journal/DiarySession/Thought || interval = "3 seconds"
            # script:
            #     sendInlineButtons($context, ["Таких мыслей нет"])
        
        state: DistortionFormulation
            q:* || fromState = "/Journal/DiarySession/Autothought"
            a: {{diary_contents.diary_distortion}}
            script:
                $session.autothought = $request.query;
                log("$session.autothought")
                log($session.autothought)
                sendInlineButtons($context, Object.keys(urls))
        
        state: RationalResponse
            q:* || fromState = "/Journal/DiarySession/DistortionFormulation"
            script:
                 $session.distortion = $request.query
                log("$session.distortion")
                log($session.distortion)
            a: {{diary_contents.diary_rational}}
            
        state: FinalEmotionIntensivity
            q:* || fromState = "/Journal/DiarySession/RationalResponse"
            a: {{diary_contents.diary_emotion_aftermath}}
            script:
                $session.rational_resp = $request.query;
                log("$session.rational_resp")
                log($session.rational_resp)
                sendInlineButtons($context, [1, 2, 3, 4, 5])
                sendInlineButtons($context, [6, 7, 8, 9, 10])
            
        state: End
            q:* || fromState = "/Journal/DiarySession/FinalEmotionIntensivity"
            script:
                $session.emotion_intensivity_after = $request.query
                log("$session.emotion_intensivity_after")
                log($session.emotion_intensivity_after)
                # $session.diaryDate = moment(now).utcOffset(180).format('YYYY-MM-DD HH:mm'));
                var now = moment()
                $client.DiaryHistory.push({"Date": moment(now).utcOffset(180).format('YYYY-MM-DD HH:mm'), "Thought": $session.thought, "Emotion": $session.emotion,
                    "Intensivity": $session.emotion_intensivity_before, "AutoThought": $session.autothought,
                    "Distortion": $session.distortion, "Rational": $session.rational_resp, "IntensivityRepeat": $session.emotion_intensivity_after});
                log($session.thought, $session.emotion, $session.emotion_intensivity_before, 
                    $session.autothought, $session.rational_resp, $session.emotion_intensivity_after)
                showDiaryNote($session.thought, $session.emotion, $session.emotion_intensivity_before, 
                    $session.autothought, $session.rational_resp, $session.emotion_intensivity_after)
            timeout: EmotionChange || interval = "2 seconds"
                
            state: EmotionChange
                script:
                    if (Number($session.emotion_intensivity_after) > Number($session.emotion_intensivity_before)) {
                        $reactions.answer(diary_contents.diary_neg_result);
                    }
                    else if (Number($session.emotion_intensivity_after) == Number($session.emotion_intensivity_before)) { 
                        $reactions.answer(diary_contents.diary_no_result);
                    }
                    
                    else {
                        $reactions.answer(diary_contents.diary_pos_result);
                    };
                timeout: /Journal/DiarySession/End/Conclusion || interval = "2 seconds"
                    
            state: Conclusion
                a: {{diary_contents.diary_session_end}}
                inlineButtons:
                    { text: "Добавить еще одну запись", callback_data: "to_journal_writing" }
                    { text: "Посмотреть историю записей", callback_data: "to_history" }
                    { text: "Посмотреть статистику по дневнику", callback_data: "to_statistics" }
                    { text: "Переформулировать негативные мысли с помощью AI", callback_data: "/reframe" }
                    { text: "Вернуться в меню", callback_data: "to_menu"}
