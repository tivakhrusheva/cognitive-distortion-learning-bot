theme: /Distortion
    
    state: Zero
        q!: (zero_distortion|distortion_zero)
        script:
            $client.cardNumber = 0
        a: Обнулились..
        
    state: CallBackProcessor
        event: telegramCallbackQuery 
        if: $request.query == "/train"
            go!: /Exercise/Start
        elseif: $request.query == "Виды искажений"
            a: {{contents.distortion_intro_to_specific}}
            timeout: /Distortion/DistortionBegin/DistortionCard || interval = "2 seconds"
        elseif: $request.query == "Distortion_next"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Дальше"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Distortion_back_to_menu" 
            go!: /Start
        elseif: $request.query == "Distortion_not_ready" || $request.query == "Вернуться в меню" 
            a: Возвращайтесь, когда будете готовы!
            go!: /Start
    
    state: DistortionBegin
        q!: $regex</learn>
        if: $client.cardNumber
            go!: DistortionCard
        else:
            a: {{contents.distortion_begin}}
            timeout: /Distortion/DistortionBegin/DistortionBegin2 || interval = "20 seconds"
        
        state: DistortionBegin2
            a: {{contents.distortion_begin2}}
            timeout: /Distortion/DistortionBegin/DistortionBegin3 || interval = "20 seconds"
        
        state: DistortionBegin3
            a: {{contents.distortion_begin3}}
            timeout: /Distortion/DistortionBegin/DistortionFightInfo || interval = "20 seconds"
        
        state: DistortionFightInfo
            q!: Катя
            a: {{contents.distortion_fight}}
            inlineButtons:
                {text: "Виды искажений", callback_data: "Виды искажений"}
                {text: "Вернуться в меню", callback_data: "Вернуться в меню"}
                
            
        state: DistortionCard
            q: Дальше || fromState = "/Distortion/DistortionBegin/DistortionCard", onlyThisState = true
            q: Виды искажений || fromState = "/Distortion/DistortionBegin/DistortionFightInfo"
            script:
                $client.cardNumber = $client.cardNumber+=1 || 0;
                # if ($context.request.channelType != "telegram") {
                sendCard($context, urls, $client.cardNumber, true);
                sendCard($context, urls_solutions, $client.cardNumber, false);
                #    }
                # else {
                #     sendMultipleCards($context, urls, urls_solutions, $client.cardNumber)
                # };
                if ($client.cardNumber < Object.keys(urls).length) {
                    $response.replies = $response.replies || [];
                    $response.replies.push(
                    {
                      "type": "inlineButtons",
                      "buttons": [
                        {
                          "text": "Дальше",
                          "callback_data": "Дальше"
                        }
                      ]
                    })
                }
                else {
                    $response.replies = $response.replies || [];
                    $response.replies.push(
                    {
                      "type": "inlineButtons",
                      "buttons": [
                        {
                          "text": "Тренироваться",
                          "callback_data": "/train"
                        }
                      ]
                    });
                }
            inlineButtons:
                {text: "Назад в меню", callback_data: "Distortion_back_to_menu"}
                    