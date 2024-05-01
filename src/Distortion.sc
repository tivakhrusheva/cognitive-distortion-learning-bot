require: functions.js
require: ./data/contents.yaml
    var = contents
require: ./data/distortions.js

theme: /Distortion
    
    state: DistortionBegin
        q!: $regex</learn>
        if: $client.cardNumber
            go!: /Distortion/DistortionCard
        else:
            a: {{contents.distortion_begin}}
            inlineButtons:
                {text: "Да", callback_data: "Distortion_ready"}
                {text: "Нет", callback_data: "Distortion_not_ready"}
    
    state: DistortionCard
        q: (Да/Distortion_ready) || fromState = "/Distortion/DistortionBegin"
        q: (Дальше/Distortion_next) || fromState = "/Distortion/DistortionCard", onlyThisState = true
        script:
            $client.cardNumber = $client.cardNumber+=1 || 0;
            sendCard($context, urls, $client.cardNumber);
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
                });
            }
        inlineButtons:
            {text: "Назад в меню", callback_data: "Distortion_back_to_menu"}
                