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
        script:
            log("$context.session.lastState")
            log($context.session.lastState)
        if: $request.query == "/train"
            go!: /Exercise/Start
            
        elseif: $request.query == "Виды искажений" && $context.session.lastState != "/Distortion/DistortionBegin/DistortionFightInfo"
            go!: /Distortion/DistortionBegin/DistortionCard 
            
        elseif: $request.query == "Виды искажений" && $context.session.lastState == "/Distortion/DistortionBegin/DistortionFightInfo"
            a: {{distortion_contents.distortion_intro_to_specific}}
            timeout: /Distortion/DistortionBegin/DistortionCard || interval = "1 seconds"
            
        elseif: $request.query == "Distortion_next" ||  $request.query == "Дальше"
            go!: /Distortion/DistortionBegin/DistortionCard
            
        elseif: ($request.query == "Вернуться в меню" && $context.session.lastState == "/Distortion/DistortionBegin/DistortionFightInfo")
            a: Возвращайтесь, когда будете готовы!
            go!: /Start
            
        elseif: ($request.query == "Вернуться в меню" && $context.session.lastState != "/Distortion/DistortionBegin/DistortionFightInfo") || ($request.query == "В меню") || ($request.query == "Distortion_back_to_menu")
            go!: /Start
            
        elseif: ($request.query == "Next_theory") && ($context.session.lastState == "/Distortion/DistortionBegin" || $context.session.lastState == "/Exercise/Answer")
            go!: /Distortion/DistortionBegin/DistortionBegin2
            
        elseif: $request.query == "regulation"
            go!: /Distortion/RegulationInfo
            
        elseif: ($request.query == "Next_theory") && ($context.session.lastState == "/Distortion/DistortionBegin/DistortionBegin2")
            go!: /Distortion/DistortionBegin/DistortionBegin3
            
        elseif: ($request.query == "Next_theory") && ($context.session.lastState == "/Distortion/DistortionBegin/DistortionBegin3")
            go!: /Distortion/DistortionBegin/DistortionFightInfo
            
        elseif: $request.query == "Теория искажений"
            a: {{distortion_contents.distortion_begin}}
            inlineButtons:
                { text: "Далее", callback_data: "theory2" }
                
        elseif: $request.query == "distortion_origin" ||  $request.query == "theory2" 
            go!: /Distortion/DistortionBegin/DistortionBegin2
            
        elseif: $request.query == "theory3"
            go!: /Distortion/DistortionBegin/DistortionBegin3
        
        elseif: $request.query == "theory4" || $request.query == "why_fight"
            go!: /Distortion/DistortionBegin/DistortionBegin4
        
        elseif: $request.query == "theory5"  || $request.query == "distortion_fight"
            go!: /Distortion/DistortionBegin/DistortionBegin5
        
        elseif: $request.query == "theory6"  || $request.query == "regulation"
            go!: /Distortion/DistortionBegin/DistortionBegin6
        
        elseif: $request.query == "theory7"  || $request.query == "regulation_techniques"
            go!: /Distortion/DistortionBegin/DistortionBegin7
        
        elseif: $request.query == "theory8"
            go!: /Distortion/DistortionBegin/DistortionBegin8
        
        elseif: $request.query == "end_theory"
            go!: /Distortion/DistortionBegin/EndTheory

    
    state: DistortionBegin
        q!: $regex</learn>
        if: $client.cardNumber
            go!: /Distortion/DistortionNavigation
        else:
            a: {{distortion_contents.distortion_begin}}
            inlineButtons:
                { text: "Далее", callback_data: "theory2" }
        
        state: DistortionBegin2
            a: {{distortion_contents.distortion_begin2}}
            inlineButtons:
                { text: "Далее", callback_data: "theory3" }
        
        state: DistortionBegin3
            a: {{distortion_contents.distortion_begin3}}
            inlineButtons:
                { text: "Далее", callback_data: "theory4" }
        
        state: DistortionBegin4
            a: {{distortion_contents.distortion_begin4}}
            inlineButtons:
                { text: "Далее", callback_data: "theory5" }
        
        state: DistortionBegin5
            a: {{distortion_contents.distortion_fight}}
            inlineButtons:
                { text: "Далее", callback_data: "theory6" }
        
        state: DistortionBegin6
            a: {{distortion_contents.regulation_info}}
            inlineButtons:
                { text: "Далее", callback_data: "theory7" }
        
        state: DistortionBegin7
            a: {{distortion_contents.regulation_technics}}
            inlineButtons:
                { text: "Далее", callback_data: "theory8" }
        
        state: DistortionBegin8
            a: {{distortion_contents.distortions_types}}
            inlineButtons:
                {text: "Виды искажений", callback_data: "Виды искажений"}
                {text: "Вернуться в меню", callback_data: "Вернуться в меню"}
                
            
        state: DistortionCard
            q: Дальше || fromState = "/Distortion/DistortionBegin/DistortionCard", onlyThisState = true
            q: Виды искажений || fromState = "/Distortion/DistortionBegin/DistortionFightInfo"
            script:
                if ($client.cardNumber >= 1 && $client.cardNumber < 10 && $context.session.lastState != "/Distortion/DistortionBegin/DistortionCard") {
                    $reactions.answer("Я верну вас к тому искажению, на котором вы остановились в прошлый раз")
                }
                $client.cardNumber = $client.cardNumber+=1 || 0;
                if ($context.request.channelType != "telegram") {
                    sendCard($context, urls, $client.cardNumber, true);
                    sendCard($context, urls_markers, $client.cardNumber, false);
                    }
                else {
                    sendMultipleCards($context, urls, urls_markers, $client.cardNumber);
                    
                };
        
        state: DistortionDistinction
            a: {{distortion_contents.distortion_difference}}
            inlineButtons:
                { text: "Далее", callback_data: "end_theory" }
            
        state: EndTheory
            script:
                $context.response.replies = $context.response.replies || [];
                $context.response.replies.push({
                    "type": "text",
                    "text": "Вы изучили всю теорию, которую я подготовил для вас! Самое время переходить к практике — для этого введите команду /practice.",
                    "markup": "html"
                });
        
                $context.response.replies.push(
                            {
                              "type": "inlineButtons",
                              "buttons": [
                                {
                                  "text": "Практика",
                                  "callback_data": "/train"
                                },
                                {
                                  "text": "Назад в меню",
                                  "callback_data": "Distortion_back_to_menu"
                                }
                              ]
                            });
    
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
            {text: "Виды искажений", callback_data: "Виды искажений"}
            {text: "Что такое когнитивные искажения", callback_data: "Теория искажений"}
            {text: "Откуда берутся когнитивные искажения", callback_data: "distortion_origin"}
            {text: "Зачем бороться с когнитивными искажениями", callback_data: "why_fight"}
            {text: "Как бороться с когнитивными искажениями", callback_data: "distortion_fight"}
            {text: "Что такое эмоциональная саморегуляция", callback_data: "regulation"}
            {text: "Техники эмоциональной саморегуляции", callback_data: "regulation_techniques"}
            {text: "Вернуться в меню", callback_data: "Вернуться в меню"}