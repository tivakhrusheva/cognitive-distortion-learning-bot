require: functions.js
require: ./data/emotions.js
require: ./data/contents.yaml
    var = contents

theme: /Diary
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Diary", onlyThisState = false
        script:
            if (emotions.includes($request.query)) {
                $reactions.transition("/Diary/EmotionIntensivity");
            }

    
    state: Start
        q!: $regex</diary>
        a: {{contents.diary_begin}}
        timeout: /Diary/Thought || interval = "2 seconds"
    
    state: Thought
        q!: $regex</diary>
        a: {{contents.diary_situation}}
    
    state: Emotion
        q!: test
        q:* || fromState = "/Diary/Thought"
        a: {{contents.diary_emotion}}
        script:
            $client.thought = $request.query;
            sendInlineButtons($context, emotions)
    
    state: EmotionIntensivity
        script: 
            $client.emotion = $request.query
        q:* || fromState = "/Diary/Emotion"
        a: {{contents.diary_emotion_intensivity}}
        script:
            sendInlineButtons($context, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    
    state: Autothought
        script: 
            $client.emotion_intensivity = $request.query
        q:* || fromState = "/Diary/EmotionIntensivity"
        a: {{contents.diary_authothought}}
    
    state: NoThought
        intent: /нет
        #a: 
    
    state: DistortionFormulation
        q:* || fromState = "/Diary/Autothought"
        a: {{contents.diary_distortion}}
    
    state: RationalResponse
        q:* || fromState = "/Diary/DistortionFormulation"
        a: {{contents.diary_rational}}
        
    state: FinalEmotionIntensivity
        q:* || fromState = "/Diary/RationalResponse"
        a: {{contents.diary_emotion_aftermath}}
    
