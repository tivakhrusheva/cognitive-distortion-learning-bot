require: functions.js
require: ./data/contents.yaml
    var = contents

theme: /Diary
    
    state: Start
        q!: $regex</diary>
        a: {{contents.diary_begin}}
        timeout: /Diary/Thought || interval = "5 seconds"
    
    state: Thought
        q!: $regex</diary>
        a: {{contents.diary_first_query}}
    
    state: Feeling
        q:* || fromState = "/Diary/Thought"
        a: {{contents.diary_first_query}}
    
    
