function sendCard(context, object, i, caption) {
    log("obj " + object);
    log("index " + i);
    context.response.replies = context.response.replies || [];
    if (i < Object.keys(object).length) {
        if (!caption) {
            context.response.replies.push({
                "type": "image",
                "imageUrl": object[Object.keys(object)[i]],
                "imageName": "<u>" + Object.keys(object)[i] + "</u>",
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
    log(buttonsNames)
    var buttons = [];
    log(buttons)
    for (var i = 0; i < buttonsNames.length; i++) {
        buttons.push({
          "text": buttonsNames[i],
          "callback_data": buttonsNames[i],
    })
    };
    log("buttons")
    log(toPrettyString(buttons))
    context.response.replies.push(
        {
        "type": "inlineButtons",
        "buttons": buttons
        }
    )
};


function sendMultipleCards(context, object1, object2, i) {
    log("obj " + object1);
    log("index " + i);
    context.response.replies = context.response.replies || [];
    
    if (i < Object.keys(object1).length) {
        context.response.replies.push({
          "type": "raw",
          "body": {
          "media": [
            {
            "type": "photo",
            "media": object1[Object.keys(object1)[i]],
            "caption": "<u>" + Object.keys(object1)[i] + "</u>",
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
            "text": "Вы изучили все искажения, которые мне известны! Время переходить к практике.\n Введите команду /train.",
            "markup": "html"
        })
    }
};

function predict(question) {
    var body = 
        {
        "clientId":"foobar",
        "input": SYSTEM_PROMPT + "Автоматическая мысль: " + question + " Рациональный ответ:"
    };
    log("body")
    log(body)

    var result = $http.post("/predict?configId=499", {
        body: body,
        dataType: "json",
    });

    if (result.isOk && result.data.output[0]) {
        return result.data.output[0].answer || null;
    }
    return null;
}

_.sample = _.wrap(_.sample, function(_sample, array, n) {
    return $jsapi.context().testContext ? (n ? array.slice(0, n) : array[0]) : _sample(array, n);
});
