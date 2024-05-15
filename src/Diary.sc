require: ./data/diary_contents.yaml
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
        if: $request.query == "Нет"
            go!: /Journal/DiarySession/NoSituation
    
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
    
    state: Start
        q!: $regex</journal>
        q!: $regex</diary>
        if: $client.diaryExplanationDone == 1
            go!: /Journal/DiarySession/Beginning
            
        a: {{diary_contents.diary_session_beg}}\n\n{{diary_contents.diary_begin}}
        inlineButtons:
            { text: "Далее", callback_data: "to_explanation" }
        # timeout: /Journal/Start/Explanation || interval = "5 seconds"
    
        state: Explanation
            a: {{diary_contents.diary_automatic_thoughts}}
            inlineButtons:
                { text: "Далее", callback_data: "to_goal" }
            #timeout: /Journal/Start/DiaryGoal || interval = "5 seconds"
        
        state: DiaryGoal
            a: {{diary_contents.diary_goal}}
            inlineButtons:
                { text: "Далее", callback_data: "to_instructions" }
            #timeout: /Journal/Start/DiaryInstructions || interval = "5 seconds"
        
        state: DiaryInstructions
            a: {{diary_contents.diary_instructions}}
            inlineButtons:
                { text: "Далее", callback_data: "to_agreement" }
            #timeout: /Journal/Start/Agreement || interval = "5 seconds"
        
        state: Agreement
            a: {{diary_contents.diary_readiness}}
            script:
                sendInlineButtons($context, ["Да", "Нет"])
    
    state: DiarySession
        
        state: Beginning
            a: {{diary_contents.diary_session_beg}}
            timeout: /Journal/DiarySession/Thought || interval = "2 seconds"
        
        state: Thought
            q:* || fromState = "/Journal/DiarySession/NoThought"
            q: Да || fromState = "/Journal/Start/Agreement"
            a: {{diary_contents.diary_situation}}
            script:
                sendInlineButtons($context, ["Нет"])
        
        state: NoSituation
            intent: /нет || fromState = "/Journal/DiarySession/Thought"
            a: {{diary_contents.diary_no_situation}}
            script:
                sendInlineButtons($context, ["Вернуться в меню"])
        
        state: Emotion
            q:* || fromState = "/Journal/DiarySession/Thought"
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
        
        state: NoThought
            intent: /нет || fromState = "/Journal/DiarySession/Autothought"
            a: {{diary_contents.diary_no_thoughts_head_empty}}
            script:
                sendInlineButtons($context, ["Да", "Нет"])
        
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
                $client.DiaryHistory.push({"Thought": $session.thought, "Emotion": $session.emotion,
                    "Intensivity": $session.emotion_intensivity_before, "AutoThought": $session.autothought,
                    "Rational": $session.rational_resp, "IntensivityRepeat": $session.emotion_intensivity_after});
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
                timeout:: /Start || interval = "5 seconds"
            
