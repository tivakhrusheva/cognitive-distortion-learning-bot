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
require: ./data/practice_contents.yaml
    var = exercise_contents
    
  
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
    # );
    bind("postProcess", function($context) {
        $context.session.lastState = $context.currentState;
    });


 
theme: /
    
    state: NewUserImitation
        q!: secret_state_new_user
        script: 
            delete $client.name;
            delete $client.cardNumber;
            delete $client.QuizQuestinNumber;
            delete $client.DiaryHistory;
            delete $client.diaryExplanationDone
        a: готово Танечка

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
                $client.cardNumber = 0;
                $client.QuizQuestinNumber = 1;
                $client.DiaryHistory = [];
            a: Здравствуйте, {{$client.name}}!😊\n\n{{contents.start}} 
            timeout: CommandDescription || interval = "4 seconds"
        else:
            go!: CommandDescription
        
        state: CommandDescription
            a: <b>Навигация</b>: \n\n{{contents.nagivation}}
            script:
                log("$client")
                log($client)

    state: NoMatch || noContext = true
        event!: noMatch
        a: Простите, я вас не понял :( Пожалуйста, переформулируйте ваш запрос.
        script:
            log($request)

    state: Unauthorized
        a: У вас нет доступа к этому боту :) 
    
    state: Array
        q!: 1
        q!: 2
        q!: 3
        q!: 4
        q!: 5
        q!: 6
        q!: 7
        q!: 8
        q!: 9
        q!: 10
        script:
            var QuizQuestinNumber = $request.query
            var corr_answer_remark = exercise_contents["quiz_correct" + QuizQuestinNumber];
            var incorr_answer_remark = exercise_contents["quiz_incorrect" + QuizQuestinNumber];
            log("corr_answer_remark")
            log(corr_answer_remark)
            log("incorr_answer_remark")
            log(incorr_answer_remark)
            if (corr_answer_remark instanceof Array) {
                corr_answer_remark =_.sample(corr_answer_remark, 1);   
                var corr_answer_remark2 =_.sample(corr_answer_remark, 1);   
            }
            if (incorr_answer_remark instanceof Array) {
                incorr_answer_remark =_.sample(incorr_answer_remark, 1);   
                var incorr_answer_remark2 =_.sample(incorr_answer_remark, 1);   
            }
            $reactions.answer(corr_answer_remark);
            $reactions.answer(corr_answer_remark2);
            $reactions.answer(incorr_answer_remark);
            $reactions.answer(incorr_answer_remark2)