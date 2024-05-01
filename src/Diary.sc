require: functions.js
require: ./data/emotions.js
require: ./data/contents.yaml
    var = contents

theme: /Diary
    
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
            sendInlineButtons($context, emotions)
    
    state: EmotionIntensivity
        q:* || fromState = "/Diary/Emotion"
        a: {{contents.diary_emotion_intensivity}}
    
    state: Autothought
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
    
