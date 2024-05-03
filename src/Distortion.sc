theme: /Distortion
    
    state: Zero
        q!: zero_distortion
        script:
            $client.cardNumber = 0
        a: Обнулились..
        
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Distortion/DistortionBegin", onlyThisState = false
        if: $request.query == "/train"
            go!: /Exercise/Start
        elseif: $request.query == "Distortion_ready"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Distortion_next"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Distortion_back_to_menu"
            go!: /Start
    
    state: DistortionBegin
        q!: $regex</learn>
        if: $client.cardNumber
            go!: DistortionCard
        else:
            a: {{contents.distortion_begin}}
            inlineButtons:
                {text: "Да", callback_data: "Distortion_ready"}
                {text: "Нет", callback_data: "Distortion_not_ready"}
            
        state: DistortionCard
            q: Да || fromState = "/Distortion/DistortionBegin"
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
                          "callback_data": "Distortion_next"
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
                    