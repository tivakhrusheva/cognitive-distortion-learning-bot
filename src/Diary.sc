require: ./data/diary_contents.yaml
    var = diary_contents

theme: /Journal
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Journal/Start", onlyThisState = false
        if: $request.query == "Да"
            script:
                $client.diaryExplanationDone = 1;
            go!: /Journal/DiarySession/Thought 
                
        elseif: $request.query == "Нет"
            a: {{diary_contents.diary_later}}
            script:
                if (!$client.diaryExplanationDone) {
                    $client.diaryExplanationDone = 0;
                }
            go!: /Start/CommandDescription
            
    state: CallBackProcessor2
        event: telegramCallbackQuery || fromState = "/Journal/DiarySession", onlyThisState = false
        script:
            if (emotions.indexOf($request.query) != -1) {
                $reactions.transition("/Journal/EmotionIntensivity");
            }
            else if ($request.query >= 1 && $request.query < 10) {
                $reactions.transition("/Journal/Autothought");
            }
    
    state: Start
        q!: $regex</journal>
        q!: $regex</diary>
        if: $client.diaryExplanationDone == 1
            go!: /Journal/DiarySession/Thought
        a: {{diary_contents.diary_begin}}
        timeout: /Journal/Explanation || interval = "5 seconds"
    
        state: Explanation
            a: {{diary_contents.diary_automatic_thoughts}}
            timeout: /Journal/DiaryGoal || interval = "5 seconds"
        
        state: DiaryGoal
            a: {{diary_contents.diary_goal}}
            timeout: /Journal/DiaryGoal || interval = "1 seconds"
        
        state: Agreement
            a: {{diary_contents.diary_goal}}
            script:
                sendInlineButtons($context, ["Да", "Нет"])
    
    state: DiarySession
        
        state: Beginning
            a: {{diary_contents.diary_session_beg}}
            timeout: /Journal/DiarySession/Thought || interval = "2 seconds"
        
        state: Thought
            q:* || fromState = "/Journal/NoThought"
            a: {{diary_contents.diary_situation}}
        
        state: Emotion
            #TODO: DELETE SHORTCUT
            q!: test
            q:* || fromState = "/Journal/Thought"
            a: {{diary_contents.diary_emotion}}
            script:
                $client.thought = $request.query;
                sendInlineButtons($context, emotions)
        
        state: EmotionIntensivity
            script: 
                $client.emotion = $request.query
            q:* || fromState = "/Journal/Emotion"
            a: {{diary_contents.diary_emotion_intensivity}}
            script:
                sendInlineButtons($context, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
        
        state: Autothought
            script: 
                $client.emotion_intensivity = $request.query
            q:* || fromState = "/Journal/EmotionIntensivity"
            a: {{diary_contents.diary_authothought}}
            script:
                sendInlineButtons($context, Object.keys(urls))
        
        state: NoThought
            intent: /нет
            a: {{diary_contents.ddiary_no_thoughts_head_empty}}
            script:
                sendInlineButtons($context, ["Да", "Нет"])
        
        state: DistortionFormulation
            q:* || fromState = "/Journal/Autothought"
            a: {{diary_contents.diary_distortion}}
        
        state: RationalResponse
            q:* || fromState = "/Journal/DistortionFormulation"
            a: {{diary_contents.diary_rational}}
            
        state: FinalEmotionIntensivity
            q:* || fromState = "/Journal/RationalResponse"
            a: {{diary_contents.diary_emotion_aftermath}}
            script:
                sendInlineButtons($context, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
        
