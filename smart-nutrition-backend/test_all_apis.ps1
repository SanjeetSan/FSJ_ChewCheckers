
$BASE_AUTH   = "http://localhost:8081"
$BASE_MEAL   = "http://localhost:8082"
$BASE_SCHOOL = "http://localhost:8083"
$BASE_MSG    = "http://localhost:8084"
$BASE_GW     = "http://localhost:8080"

$results = [System.Collections.ArrayList]@()

function Test-API($name, $method, $url, $body, $headers, $expectStatus) {
    if (-not $headers) { $headers = @{} }
    if (-not $expectStatus) { $expectStatus = @(200,201) }
    $headers["Content-Type"] = "application/json"
    try {
        $params = @{ Uri = $url; Method = $method; Headers = $headers; ErrorAction = "Stop" }
        if ($body) { $params["Body"] = $body }
        $resp = Invoke-WebRequest @params
        $code = [int]$resp.StatusCode
        $ok = $expectStatus -contains $code
        return [PSCustomObject]@{ API = $name; HTTP = $code; Result = if ($ok) { "PASS" } else { "WARN" }; Notes = "" }
    } catch {
        $code = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { 0 }
        $msg = ""
        try {
            $s = $_.Exception.Response.GetResponseStream()
            $r = New-Object System.IO.StreamReader($s)
            $j = $r.ReadToEnd() | ConvertFrom-Json
            $msg = $j.message
        } catch {}
        return [PSCustomObject]@{ API = $name; HTTP = $code; Result = "FAIL"; Notes = $msg }
    }
}

Write-Host ""
Write-Host "=== AUTH SERVICE ===" -ForegroundColor Cyan

$r = Test-API "POST /api/auth/register" "POST" "$BASE_AUTH/api/auth/register" '{"name":"API Tester","email":"apitest@test.com","password":"Test@1234","role":"ADMIN"}' $null @(200,201,400,409)
$null = $results.Add($r)

$TOKEN = ""
try {
    $resp = Invoke-RestMethod -Uri "$BASE_AUTH/api/auth/login" -Method POST -Body '{"email":"apitest@test.com","password":"Test@1234"}' -ContentType "application/json"
    $TOKEN = $resp.token
    $null = $results.Add([PSCustomObject]@{ API = "POST /api/auth/login"; HTTP = 200; Result = "PASS"; Notes = "Token OK" })
    Write-Host "  Login OK - Token obtained" -ForegroundColor Green
} catch {
    $null = $results.Add([PSCustomObject]@{ API = "POST /api/auth/login"; HTTP = 0; Result = "FAIL"; Notes = $_.Exception.Message })
    Write-Host "  Login FAILED" -ForegroundColor Red
}

$H = @{ Authorization = "Bearer $TOKEN" }

$r = Test-API "POST /api/auth/refresh" "POST" "$BASE_AUTH/api/auth/refresh" "{`"token`":`"$TOKEN`"}" $null @(200,400)
$null = $results.Add($r)

$r = Test-API "PUT /api/user/profile" "PUT" "$BASE_AUTH/api/user/profile" '{"name":"Updated Tester"}' $H @(200,201)
$null = $results.Add($r)

Write-Host ""
Write-Host "=== ADMIN APIs ===" -ForegroundColor Cyan

$r = Test-API "GET /api/admin/users" "GET" "$BASE_AUTH/api/admin/users" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/admin/system/health" "GET" "$BASE_AUTH/api/admin/system/health" $null $H @(200)
$null = $results.Add($r)

Write-Host ""
Write-Host "=== GATEWAY APIs ===" -ForegroundColor Cyan

$r = Test-API "GET /api/gateway/routes" "GET" "$BASE_GW/api/gateway/routes" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/gateway/metrics" "GET" "$BASE_GW/api/gateway/metrics" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/gateway/eureka" "GET" "$BASE_GW/api/gateway/eureka" $null $H @(200)
$null = $results.Add($r)

Write-Host ""
Write-Host "=== MEAL SERVICE APIs ===" -ForegroundColor Cyan

$r = Test-API "POST /api/meals/pre-meal" "POST" "$BASE_MEAL/api/meals/pre-meal" '{"foodItems":["Rice","Dal"],"mealTime":"LUNCH","notes":"Test"}' $H @(200,201,400)
$null = $results.Add($r)

$r = Test-API "POST /api/meals/post-meal" "POST" "$BASE_MEAL/api/meals/post-meal" '{"mealId":1,"actualFoodItems":["Rice"],"hungerLevel":"SATISFIED"}' $H @(200,201,400,404)
$null = $results.Add($r)

$r = Test-API "POST /api/assistant/chat" "POST" "$BASE_MEAL/api/assistant/chat" '{"message":"What is a healthy breakfast?"}' $H @(200,201)
$null = $results.Add($r)

Write-Host ""
Write-Host "=== SCHOOL SERVICE APIs ===" -ForegroundColor Cyan

$r = Test-API "GET /api/teacher/classes" "GET" "$BASE_SCHOOL/api/teacher/classes" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/teacher/students" "GET" "$BASE_SCHOOL/api/teacher/students" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/teacher/reports/weekly" "GET" "$BASE_SCHOOL/api/teacher/reports/weekly" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/teacher/reports/monthly" "GET" "$BASE_SCHOOL/api/teacher/reports/monthly" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "POST /api/parent/student" "POST" "$BASE_SCHOOL/api/parent/student" '{"name":"Child One","dateOfBirth":"2015-05-10","grade":"3A","allergies":[],"schoolId":1}' $H @(200,201,400,404)
$null = $results.Add($r)

$r = Test-API "GET /api/parent/students" "GET" "$BASE_SCHOOL/api/parent/students" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/parent/classes" "GET" "$BASE_SCHOOL/api/parent/classes" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/school/reports/weekly/1" "GET" "$BASE_SCHOOL/api/school/reports/weekly/1" $null $H @(200,404)
$null = $results.Add($r)

Write-Host ""
Write-Host "=== MESSAGING SERVICE APIs ===" -ForegroundColor Cyan

$r = Test-API "POST /api/messages" "POST" "$BASE_MSG/api/messages" '{"receiverId":2,"content":"Hello test message"}' $H @(200,201,400,404)
$null = $results.Add($r)

$r = Test-API "GET /api/messages/history/2" "GET" "$BASE_MSG/api/messages/history/2" $null $H @(200,404)
$null = $results.Add($r)

$r = Test-API "POST /api/social/friends/request/2" "POST" "$BASE_MSG/api/social/friends/request/2" $null $H @(200,201,400,404,409)
$null = $results.Add($r)

$r = Test-API "GET /api/social/friends" "GET" "$BASE_MSG/api/social/friends" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/social/friends/requests" "GET" "$BASE_MSG/api/social/friends/requests" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "POST /api/social/recipes" "POST" "$BASE_MSG/api/social/recipes" '{"title":"Dal Rice","ingredients":["Rice","Dal"],"instructions":"Cook and mix","nutritionInfo":"Healthy"}' $H @(200,201)
$null = $results.Add($r)

$r = Test-API "GET /api/social/recipes" "GET" "$BASE_MSG/api/social/recipes" $null $H @(200)
$null = $results.Add($r)

$r = Test-API "GET /api/social/friends/feed" "GET" "$BASE_MSG/api/social/friends/feed" $null $H @(200)
$null = $results.Add($r)

Write-Host ""
Write-Host "============================================================" -ForegroundColor White
Write-Host "  SMART NUTRITION -- FULL API TEST RESULTS" -ForegroundColor White
Write-Host "============================================================" -ForegroundColor White
$results | Format-Table -AutoSize -Property API, HTTP, Result, Notes
Write-Host "============================================================" -ForegroundColor White

$pass = ($results | Where-Object { $_.Result -eq "PASS" }).Count
$fail = ($results | Where-Object { $_.Result -eq "FAIL" }).Count
$total = $results.Count

Write-Host "  PASS: $pass / $total    FAIL: $fail / $total"
Write-Host "============================================================" -ForegroundColor White

if ($fail -gt 0) {
    Write-Host ""
    Write-Host "FAILED APIs:" -ForegroundColor Red
    $results | Where-Object { $_.Result -eq "FAIL" } | ForEach-Object {
        Write-Host ("  - " + $_.API + " -> HTTP " + $_.HTTP + " | " + $_.Notes) -ForegroundColor Red
    }
}
