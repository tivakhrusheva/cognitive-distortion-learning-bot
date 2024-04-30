require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: functions.js
require: ./data/contents.yaml
    var = contents
require: Exercise.sc
require: Diary.sc
    
  
init:
    $global.USERS_TABLE = $injector.usersTable;
    bind("preMatch", function($context) {
        
    
    if ($context.request.channelType === "telegram" && $context.request.rawRequest.message.from.id != "635678009") {
        $context.temp.targetState = "/Unauthorized"
    }
    },
    "/"
    )

 
theme: /

    state: Start
        q!: $regex</start>
        a: {{contents.start}}
        a: {{contents.start_second}}

    state: NoMatch
        event!: noMatch
        a: Простите, я вас не понял :( Пожалуйста, переформулируйте ваш запрос.

    state: Match
        event!: match
        a: {{$context.intent.answer}}
    
    state: Unauthorized
        a: Вы не авторизованы