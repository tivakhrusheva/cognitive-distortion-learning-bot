require: ./data/feedback_content.yaml
    var = feedback_content

theme: /Feedback


    state: UserInput
        q!: $regex</feedback>
        a: {{feedback_content.request_feedback}}
        
        state: MerciUser
            q: * || fromState = "/Feedback/UserInput", onlyThisState = true
            a: {{feedback_content.feedback_merci}}
            go: /Start