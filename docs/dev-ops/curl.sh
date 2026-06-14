curl --location --request POST 'https://api.xiaomimimo.com/v1/chat/completions' \
--header "api-key: sk-cabc2asc0elzq4h2seehzn8dmps4b94pbwn21h2f2qfbonck" \
--header "Content-Type: application/json" \
--data-raw '{
    "model": "mimo-v2.5-pro",
    "messages": [
        {
            "role": "user",
            "content": "1+1"
        }
    ]
}'
