theme: /Exercise
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Exercise/Start/Continue", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Exercise/Predict/Answer", onlyThisState = true
        if: $request.query == "Да"
            script:
                $client.agreeAI = 1;
            go!: /Exercise/UserInput
        elseif: $request.query == "Нет"
            go!: /Start
        elseif: $request.query == "Задать еще вопрос"
            go!: /Exercise/UserInput
        elseif: $request.query == "Вернуться в меню"
            go!: /Start

    state: Start
        q!: $regex</reframe>
        q!: $regex</ask>
        q!: Тренироваться
        a: {{contents.exercise}}
        if: $client.agreeAI
            timeout: Continue || interval = "1 seconds"
        else: 
            timeout: /Exercise/UserInput || interval = "1 seconds"
        
        state: Continue
            a: Желаете продолжить?
            inlineButtons:
                {text: "Да", callback_data: "Да"}
                {text: "Нет", callback_data: "Нет"}
        
    state: UserInput
        q: Да ||fromState = "/Exercise/Start/Continue"
        a: Пожалуйста, введите ваш запрос:
        timeout: Question || interval = "2 seconds"
    
        state: Question
            random:
                a: Примеры негативных мыслей:
                a: Примеры автоматических мыслей:
            script:
                log(EXAMPLE_QUESTIONS)
                log(_.sample(EXAMPLE_QUESTIONS, 2))
                $reactions.buttons(_.sample(EXAMPLE_QUESTIONS, 2));
    
    state: Predict
        q: * || fromState = "/Exercise/UserInput", onlyThisState = false
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
            inlinebuttons:
                {text: "Задать еще вопрос", callback_data: "Задать еще вопрос"}
                {text: "Вернуться в меню", callback_data: "Вернуться в меню"}

        state: Error
            random:
                a: Извините, произошла ошибка. Попробуйте спросить позже.
                a: К сожалению, я не смог обработать ваш запрос из-за ошибки. Я передам ее разработчикам.
                a: Внутри меня что-то сломалось. Пожалуйста, задайте ваш вопрос еще раз.
            script:
                $analytics.setSessionResult("Error");
                $jsapi.stopSession();
                


    
