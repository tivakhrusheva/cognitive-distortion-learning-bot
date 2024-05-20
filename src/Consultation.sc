theme: /Consultation
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Consultation/Start/Continue", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Consultation/Predict/Answer", onlyThisState = true
        if: $request.query == "Да"
            script:
                $client.agreeAI = 1;
                log("soqua i added 1!!!")
            go!: /Consultation/UserInput
        elseif: $request.query == "Нет"
            go!: /Start
        elseif: $request.query == "Задать еще вопрос"
            go!: /Consultation/UserInput
        elseif: $request.query == "Вернуться в меню"
            a: Был рад помочь!
            timeout: /Start || interval = "3 seconds"
    
    state: CallBackProcessor2
        q: Вернуться в меню || fromState = "/Consultation/UserInput/Question", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Consultation/UserInput", onlyThisState = false
        if: $request.query == "Вернуться в меню"
            a: Возвращайтесь, если захотите попробовать данный функционал!
            timeout: /Start || interval = "3 seconds"
        elseif: $request.query == "use_last_request"
            script:
                $request.query = "Мысль: " + "\n" + $session.thought + ".\n" + "Я чувствую " + $session.emotion + ".\n" + "Я думаю, что" + $session.autothought
            go!: /Consultation/Predict
        else:
            go!: /Consultation/Predict

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
        q: Да || fromState = "/Consultation/Start/Continue"
        go!: Question 
    
        state: Question
            a: Пожалуйста, опишите ситуацию и ваши негативные мысли по ее поводу, или намите на кнопку "Использовать прошлый запрос", чтобы в качестве запроса был отправлен ваш последний запрос из дневника автоматических мыслей и искажений: \n\n "Мысль: {{$session.thought}}.\nЯ чувствую {{$session.emotion}}.\nЯ думаю, что {{$session.autothought}}.
            inlineButtons:
                { text: "Использовать прошлый запрос", callback_data: "use_last_request" }
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
                


    
