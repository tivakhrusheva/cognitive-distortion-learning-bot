require: functions.js
require: ./data/emotions.js
require: ./data/contents.yaml
    var = contents

theme: /Journal
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Journal", onlyThisState = false
        script:
            if (emotions.indexOf($request.query) != -1) {
                $reactions.transition("/Journal/EmotionIntensivity");
            }
            else if ($request.query >= 1 && $request.query < 10) {
                $reactions.transition("/Journal/Autothought");
            }

    
    state: Start
        q!: $regex</journal>
        a: {{contents.diary_begin}}
        timeout: /Journal/Thought || interval = "1 seconds"
    
    state: Thought
        q!: $regex</diary>
        a: {{contents.diary_situation}}
    
    state: Emotion
        q!: test
        q:* || fromState = "/Journal/Thought"
        a: {{contents.diary_emotion}}
        script:
            $client.thought = $request.query;
            sendInlineButtons($context, emotions)
    
    state: EmotionIntensivity
        script: 
            $client.emotion = $request.query
        q:* || fromState = "/Journal/Emotion"
        a: {{contents.diary_emotion_intensivity}}
        script:
            sendInlineButtons($context, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    
    state: Autothought
        script: 
            $client.emotion_intensivity = $request.query
        q:* || fromState = "/Journal/EmotionIntensivity"
        a: {{contents.diary_authothought}}
    
    state: NoThought
        intent: /нет
        #a: 
    
    state: DistortionFormulation
        q:* || fromState = "/Journal/Autothought"
        a: {{contents.diary_distortion}}
    
    state: RationalResponse
        q:* || fromState = "/Journal/DistortionFormulation"
        a: {{contents.diary_rational}}
        
    state: FinalEmotionIntensivity
        q:* || fromState = "/Journal/RationalResponse"
        a: {{contents.diary_emotion_aftermath}}
    
