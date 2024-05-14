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
            
    state: CallBackProcessorEmotion
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/Emotion", onlyThisState = true
        script:
            $session.emotion = $request.query
        go!: /Journal/DiarySession/EmotionIntensivity
    
    state: CallBackProcessorEmotionIntensivity
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/EmotionIntensivity", onlyThisState = true
        script:
            $session.emotion_intensivity_before = $request.query
        go!: /Journal/DiarySession/Autothought
    
    state: CallBackProcessorDistortion
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/DistortionFormulation", onlyThisState = true
        script:
            $session.distortion = $request.query
        go!: /Journal/DiarySession/RationalResponse
    
    state: CallBackProcessorEmotionIntensivityAfter
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession/FinalEmotionIntensivity", onlyThisState = true
        script:
            $session.emotion_intensivity_after = $request.query
        go!: /Journal/DiarySession/End
    
    state: Start
        q!: $regex</journal>
        q!: $regex</diary>
        if: $client.diaryExplanationDone == 1
            go!: /Journal/DiarySession/Beginning
        a: {{diary_contents.diary_session_beg}}\n\n{{diary_contents.diary_begin}}

        timeout: /Journal/Start/Explanation || interval = "5 seconds"
    
        state: Explanation
            a: {{diary_contents.diary_automatic_thoughts}}
            timeout: /Journal/Start/DiaryGoal || interval = "5 seconds"
        
        state: DiaryGoal
            a: {{diary_contents.diary_goal}}
            timeout: /Journal/Start/DiaryInstructions || interval = "5 seconds"
        
        state: DiaryInstructions
            a: {{diary_contents.diary_instructions}}
            timeout: /Journal/Start/Agreement || interval = "5 seconds"
        
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
            a: {{diary_contents.diary_situation}}
        
        state: Emotion
            #TODO: DELETE SHORTCUT
            q!: test
            q:* || fromState = "/Journal/DiarySession/Thought"
            a: {{diary_contents.diary_emotion}}
            script:
                $session.thought = $request.query;
                sendInlineButtons($context, emotions)
        
        state: EmotionIntensivity
            q:* || fromState = "/Journal/DiarySession/Emotion"
            a: {{diary_contents.diary_emotion_intensivity}}
            script:
                sendInlineButtons($context, [1, 2, 3, 4, 5])
                sendInlineButtons($context, [6, 7, 8, 9, 10])
        
        state: Autothought
            q:* || fromState = "/Journal/DiarySession/EmotionIntensivity"
            a: {{diary_contents.diary_autothought}}
        
        state: NoThought
            intent: /нет
            a: {{diary_contents.ddiary_no_thoughts_head_empty}}
            script:
                sendInlineButtons($context, ["Да", "Нет"])
        
        state: DistortionFormulation
            q:* || fromState = "/Journal/DiarySession/Autothought"
            a: {{diary_contents.diary_distortion}}
            script:
                $session.autothought = $request.query;
                sendInlineButtons($context, Object.keys(urls))
        
        state: RationalResponse
            q:* || fromState = "/Journal/DiarySession/DistortionFormulation"
            a: {{diary_contents.diary_rational}}
            
        state: FinalEmotionIntensivity
            q:* || fromState = "/Journal/DiarySession/RationalResponse"
            a: {{diary_contents.diary_emotion_aftermath}}
            script:
                $session.rational_resp = $request.query;
                sendInlineButtons($context, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
            
        state: End
            script:
                if ($session.emotion_intensivity_after > $session.emotion_intensivity_before) {
                    $reactions.answer(diary_contents.diary_neg_result);
                }
                else if ($session.emotion_intensivity_after == $session.emotion_intensivity_before) { 
                    $reactions.answer(diary_contents.diary_no_result);
                }
                
                else {
                    $reactions.answer(diary_contents.diary_pos_result);
                }

            a: {{diary_contents.diary_session_end}}
            timeout:: /Start/CommandDescription || interval = "5 seconds"
        
