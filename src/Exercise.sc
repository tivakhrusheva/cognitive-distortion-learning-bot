require: functions.js

theme: /Exercise
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Exercise/Start/Continue", onlyThisState = true
        if: $request.query == "Да"
            go!: /Exercise/UserInput
        elseif: $request.query == "Нет"
            go!: /Start

    state: Start
        q!: $regex</train>
        q!: Тренироваться
        a: {{contents.exercise}}
        timeout: Continue || interval = "1 seconds"
        
        state: Continue
            a: Желаете продолжить?
            inlineButtons:
                {text: "Да", callback_data: "Да"}
                {text: "Нет", callback_data: "Нет"}
        
    state: UserInput
        q: Да ||fromState = "/Exercise/Start/Continue"
        a: Пожалуйста, введите ваш запрос:
        timeout: /Exercise/Question || interval = "2 seconds"
    
    state: Question
        random:
            a: Можете также посмотреть на примеры формулировок негативных мыслей:
            a: Примеры формулировок:
        script:
            $reactions.buttons(_.sample(EXAMPLE_QUESTIONS, 2));
    
    state: Predict
        q!: *
        script:
            if (!$context.testContext) {
                $conversationApi.sendTextToClient(
                    _.sample(
                        [
                            "Готовлю ответ. Подождите, пожалуйста…",
                            "Одну минуточку, подождите…"
                        ]
                    )
                );
            }
            $temp.result = predict($request.query);
        if: $temp.result !== null
            go!: ./Answer
        else:
            go!: ./Error

        state: Answer
            a: {{$temp.result}}
            script:
                $analytics.setSessionResult("Answer");
                $analytics.setSessionData("Answer", $temp.result);
            go!: /Actions

        state: Error
            random:
                a: Извините, произошла ошибка. Попробуйте спросить позже.
                a: К сожалению, я не смог обработать ваш запрос из-за ошибки. Я передам ее разработчикам.
                a: Внутри меня что-то сломалось. Пожалуйста, задайте ваш вопрос еще раз.
            script:
                $analytics.setSessionResult("Error");
                $jsapi.stopSession();

    state: Actions
        buttons:
            "Задать еще вопрос" -> /Question

    
