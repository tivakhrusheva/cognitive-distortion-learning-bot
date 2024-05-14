require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: functions.js
require: ./data/distortions.js
require: ./data/emotions.js
require: ./data/contents.yaml
    var = contents
require: Diary.sc
require: Distortion.sc
require: Exercise.sc
require: Consultation.sc
require: Settings.sc
    
  
init:
    $global.USERS_TABLE = $injector.usersTable;
    $global.MY_ID = $injector.myId;
    $global.SYSTEM_PROMPT = $injector.prompt;
    $global.EXAMPLE_QUESTIONS = $injector.exampleQuestions;
    bind("postProcess", function(context) {
    if (context.request.channelType === "telegram") {
        if (context.response.replies) {
        context.response.replies.forEach(function(reply) {
            if (reply.type === "text") {
                reply.markup = "html";
            }
            });
        }
    }
    });
    # $http.config($injector.httpConfig);
    
    # bind("onAnyError", function($context) {
    #     log();
    #     var answers = [
    #         "Ой, что-то пошло не так.. Мы уже исправляем проблему, попробуйте зайти немного позже!",
    #         "Кажется, произошла какая-то ошибка. Пожалуйста, повторите запрос немного позже!"
    #     ];
    #     var randomAnswer = answers[$reactions.random(answers.length)];
    #     $reactions.answer(randomAnswer);
    # });
    
    # bind("preMatch", function($context) {
    #     log($context)
    # if ($context.request.channelType === "telegram" && $context.request.data.chatId != MY_ID) {
    #     $context.temp.targetState = "/Unauthorized"
    # }
    # },
    # "/"
    # )

 
theme: /

    state: Start
        q!: $regex</start>
        if: $context.request.channelType == "chatwidget"
            script:
                $client.name = "Таня"
        script:
            log($context.request.channelType);
            $jsapi.startSession();
        if: (!$client.name && $context.request.channelType == "telegram")
            script:
                $client.name = $request.rawRequest.message.from.first_name;
                $client.cardNumber = 0
            a: Здравствуйте, {{$client.name}}😊!\n\n{{contents.start}} 
            timeout: CommandDescription || interval = "3 seconds"
        else:
            go!: CommandDescription
        
        state: CommandDescription
            a: <b>Навигация</b>: \n\n{{contents.nagivation}}

    state: NoMatch
        event!: noMatch
        a: Простите, я вас не понял :( Пожалуйста, переформулируйте ваш запрос.
        script:
            log($request)

    state: Unauthorized
        a: У вас нет доступа к этому боту :) 