require: functions.js
require: ./data/contents.yaml
    var = contents

theme: /Diary
    
    state: Start
        q!: $regex</diary>
        a: Начнём.
