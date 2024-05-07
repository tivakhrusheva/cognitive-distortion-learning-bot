theme: /Consultation
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Consultation/Start/Continue", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Consultation/Predict/Answer", onlyThisState = true
        if: $request.query == "Да"
            script:
                $client.agreeAI = 1;
            go!: /Consultation/UserInput
        elseif: $request.query == "Нет"
            go!: /Start
        elseif: $request.query == "Задать еще вопрос"
            go!: /Consultation/UserInput
        elseif: $request.query == "Вернуться в меню"
            a: Были рады помочь!
            timeout: /Start || interval = "3 seconds"
    
    state: CallBackProcessor2
        q: Вернуться в меню || fromState = "/Consultation/UserInput/Question", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Consultation/UserInput", onlyThisState = false
        if: $request.query == "Вернуться в меню"
            a: Возвращайтесь, если захотите попробовать данный функционал!
            timeout: /Start || interval = "3 seconds"

    state: Start
        q!: $regex</reframe>
        q!: $regex</ask>
        q!: Тренироваться
        a: {{contents.consultation_info}}
        if: $client.agreeAI
            timeout: Continue || interval = "1 seconds"
        else: 
            timeout: /Consultation/UserInput || interval = "1 seconds"
        
        state: Continue
            a: Желаете продолжить?
            inlineButtons:
                {text: "Да", callback_data: "Да"}
                {text: "Нет", callback_data: "Нет"}
        
    state: UserInput
        q: Да ||fromState = "/Consultation/Start/Continue"
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
            inlineButtons:
                { text: "Вернуться в меню", callback_data: "Вернуться в меню" }
            
    state: Predict
        q: * || fromState = "/Consultation/UserInput", onlyThisState = false
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
            inlineButtons:
                {text: "Задать еще вопрос", callback_data: "Задать еще вопрос"}
                {text: "Вернуться в меню", callback_data: "Вернуться в меню"}
            # script:
            #     $analytics.setSessionResult("Answer");
            #     $analytics.setSessionData("Answer", $temp.result);

        state: Error
            random:
                a: Извините, произошла ошибка. Попробуйте спросить позже.
                a: К сожалению, я не смог обработать ваш запрос из-за ошибки. Я передам ее разработчикам.
                a: Внутри меня что-то сломалось. Пожалуйста, задайте ваш вопрос еще раз.
            script:
                $analytics.setSessionResult("Error");
                $jsapi.stopSession();
                


    
