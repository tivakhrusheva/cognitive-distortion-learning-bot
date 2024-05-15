require: ./data/distortion_contents.yaml
    var = distortion_contents

theme: /Distortion
    
    state: DistortionExplanation
        intent!: /distortions/Катастрофизация
        intent!: /distortions/Негативный фильтр
        intent!: /distortions/Долженствование
        intent!: /distortions/Иллюзия контроля
        intent!: /distortions/Навешивание ярлыков
        intent!: /distortions/Персонализация
        intent!: /distortions/Сверхобобщение
        intent!: /distortions/Обесценивание
        intent!: /distortions/Долженствование
        intent!: /distortions/Чтение мыслей
        script:
            var intentName = $context.nluResults.intents[0].debugInfo.intent.path.split('/')[2]
            $context.response.replies = $context.response.replies || [];
            $context.response.replies.push({
              "type": "raw",
              "body": {
              "media": [
                {
                "type": "photo",
                "media": urls[intentName],
                "parse_mode": "html"},
                {
                "type": "photo",
                "media": urls_solutions[intentName],
                }
            ]},
              "method": "sendMediaGroup"
            });
            $reactions.answer("<b>" + intentName + "</b>");
    
    state: Zero
        q!: (zero_distortion|distortion_zero)
        script:
            $client.cardNumber = 0
        a: Обнулились..
        
    state: CallBackProcessor
        event!: telegramCallbackQuery 
        if: $request.query == "/train"
            go!: /Exercise/Start
        elseif: $request.query == "Виды искажений" && $context.session.lastState != "/Distortion/DistortionBegin/DistortionFightInfo"
            go!: /Distortion/DistortionBegin/DistortionCard 
        elseif: $request.query == "Виды искажений" && $context.session.lastState == "/Distortion/DistortionBegin/DistortionFightInfo"
            a: {{distortion_contents.distortion_intro_to_specific}}
            timeout: /Distortion/DistortionBegin/DistortionCard || interval = "1 seconds"
        elseif: $request.query == "Distortion_next"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Дальше"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Distortion_back_to_menu" 
            go!: /Start
        elseif: ($request.query == "Вернуться в меню" && $context.session.lastState == "/Distortion/DistortionBegin/DistortionFightInfo")
            a: Возвращайтесь, когда будете готовы!
            go!: /Start
        elseif: ($request.query == "Вернуться в меню" && $context.session.lastState != "/Distortion/DistortionBegin/DistortionFightInfo")
            go!: /Start
        elseif: ($request.query == "Next_theory") && ($context.session.lastState == "/Distortion/DistortionBegin")
            go!: /Distortion/DistortionBegin/DistortionBegin2
        elseif: $request.query == "regulation"
            go!: /Distortion/RegulationInfo
        elseif: ($request.query == "Next_theory") && ($context.session.lastState == "/Distortion/DistortionBegin/DistortionBegin2")
            go!: /Distortion/DistortionBegin/DistortionBegin3
        elseif: ($request.query == "Next_theory") && ($context.session.lastState == "/Distortion/DistortionBegin/DistortionBegin3")
            go!: /Distortion/DistortionBegin/DistortionFightInfo
        elseif: $request.query == "В меню"
            go!: /Start/CommandDescription
        elseif: $request.query == "Теория искажений"
            a: {{distortion_contents.distortion_begin}}
            inlineButtons:
                { text: "Далее", callback_data: "theory2" }
            # go!: /Distortion/DistortionBegin
        elseif: $request.query == "theory2"
            go!: /Distortion/DistortionBegin/DistortionBegin2
        elseif: ($request.query == "Next_regulation")  && ($context.session.lastState == "/Distortion/RegulationInfo")
            go!: /Distortion/RegulationInfo/RegulationTechniques
    
    state: DistortionBegin
        q!: $regex</learn>
        if: $client.cardNumber
            go!: /Distortion/DistortionNavigation
        else:
            a: {{distortion_contents.distortion_begin}}
            inlineButtons:
                { text: "Далее", callback_data: "Next_theory" }
        
        state: DistortionBegin2
            a: {{distortion_contents.distortion_begin2}}
            inlineButtons:
                { text: "Далее", callback_data: "Next_theory" }
        
        state: DistortionBegin3
            a: {{distortion_contents.distortion_begin3}}
            inlineButtons:
                { text: "Далее", callback_data: "Next_theory" }
        
        state: DistortionFightInfo
            a: {{distortion_contents.distortion_fight}}
            inlineButtons:
                {text: "Виды искажений", callback_data: "Виды искажений"}
                {text: "Вернуться в меню", callback_data: "Вернуться в меню"}
                
            
        state: DistortionCard
            q: Дальше || fromState = "/Distortion/DistortionBegin/DistortionCard", onlyThisState = true
            q: Виды искажений || fromState = "/Distortion/DistortionBegin/DistortionFightInfo"
            script:
                log("$client.cardNumber")
                log($client.cardNumber)
                $client.cardNumber = $client.cardNumber+=1 || 0;
                log("$client.cardNumber NOW")
                log($client.cardNumber)
                if ($context.request.channelType != "telegram") {
                    sendCard($context, urls, $client.cardNumber, true);
                    sendCard($context, urls_solutions, $client.cardNumber, false);
                    }
                else {
                    sendMultipleCards($context, urls, urls_solutions, $client.cardNumber);
                    
                };
    
    state: RegulationInfo
        a: {{distortion_contents.regulation_info}}
        inlineButtons:
            { text: "Далее", callback_data: "Next_regulation" }
            
        state: RegulationTechniques
            a: {{distortion_contents.regulation_technics}}
            inlineButtons:
                {text: "Вернуться в меню", callback_data: "Вернуться в меню"}
        
    
    state: DistortionNavigation
        a: ⬇️Выберите раздел теории:
        inlineButtons:
            {text: "Теория искажений", callback_data: "Теория искажений"}
            {text: "Виды искажений", callback_data: "Виды искажений"}
            {text: "Техники эмоциональной саморегуляции", callback_data: "regulation"}
            {text: "Вернуться в меню", callback_data: "Вернуться в меню"}