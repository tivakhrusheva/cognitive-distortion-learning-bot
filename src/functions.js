function sendCard(context, object, i, caption) {
    context.response.replies = context.response.replies || [];
    if (i < Object.keys(object).length) {
        if (!caption) {
            context.response.replies.push({
                "type": "image",
                "imageUrl": object[Object.keys(object)[i]],
                "imageName": "<b>" + Object.keys(object)[i] + "</b>",
                "markup": "html"
            })
        }
        else {
            context.response.replies.push({
                "type": "image",
                "imageUrl": object[Object.keys(object)[i]]
            })
        }}
    else {
            context.response.replies.push({
                "type": "text",
                "text": "Вы изучили все искажения, которые мне известны! Время переходить к практике.\n Введите команду /train.",
                "markup": "html"
            })
        }
};


function sendInlineButtons(context, buttonsNames) {
    context.response.replies = context.response.replies || [];
    var buttons = [];
    for (var i = 0; i < buttonsNames.length; i++) {
        buttons.push({
          "text": buttonsNames[i],
          "callback_data": buttonsNames[i],
    })
    };
    context.response.replies.push(
        {
        "type": "inlineButtons",
        "buttons": buttons
        }
    )
};


function sendMultipleCards(context, object1, object2, i) {
    context.response.replies = context.response.replies || [];
    
    if (i < Object.keys(object1).length) {
        context.response.replies.push({
          "type": "raw",
          "body": {
          "media": [
            {
            "type": "photo",
            "media": object1[Object.keys(object1)[i]],
            "caption": "<b>" + Object.keys(object1)[i] + "</b>",
            "parse_mode": "html"},
            {
            "type": "photo",
            "media": object2[Object.keys(object2)[i]],
            }
        ]},
          "method": "sendMediaGroup"
        })
    }
    else {
        context.response.replies.push({
            "type": "text",
            "text": "Вы изучили все искажения, которые мне известны! Самое время переходить к практике — для этого введите команду /practice.",
            "markup": "html"
        })
    }
};

function predict(question) {
    var body = 
        {
        "clientId":"foobar",
        "input": SYSTEM_PROMPT + "Автоматическая мысль: " + question
    };
    var headers = {
        "Content-Type": "application/json",
        "MLP-API-KEY": $secrets.get("MLP_API_KEY")
    };
    log("body")
    log(body)

    var result = $http.post("https://caila.io/api/mlpgate/account/1000062767/model/51022/predict?configId=499", {
        body: body,
        dataType: "json",
        headers: headers
    });
    
    if (result.isOk && result.data.replies[0]) {
        return result.data.replies[0].text || null;
    }
    return null;
}

_.sample = _.wrap(_.sample, function(_sample, array, n) {
    return $jsapi.context().testContext ? (n ? array.slice(0, n) : array[0]) : _sample(array, n);
});
