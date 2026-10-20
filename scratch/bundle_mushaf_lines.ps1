# Bundles per-line mushaf layout data (word text + line slots) for all 604 Madani pages
# Source: Quran.com API v4 (line segmentation mirrors the official KFGQPC Madinah mushaf).
param(
    [int]$From = 1,
    [int]$To = 604
)
$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$outDir = Join-Path $PSScriptRoot "..\app\src\main\assets\mushaf_lines"
if (-not (Test-Path $outDir)) { New-Item -ItemType Directory -Path $outDir | Out-Null }
$utf8 = New-Object System.Text.UTF8Encoding($false)

function Get-Page($p) {
    $url = "https://api.quran.com/api/v4/verses/by_page/$($p)?fields=text_uthmani&words=true&word_fields=text_uthmani,line_number,char_type_name&per_page=60"
    for ($try = 1; $try -le 4; $try++) {
        try {
            return Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 40
        } catch {
            if ($try -eq 4) { throw }
            Start-Sleep -Milliseconds (600 * $try)
        }
    }
}

$ok = 0; $fail = @(); $maxLineGlobal = 0
for ($p = $From; $p -le $To; $p++) {
    $file = Join-Path $outDir ("p{0:d3}.json" -f $p)
    if (Test-Path $file) { $ok++; continue }
    try {
        $page = Get-Page $p
        $lines = @{}
        $suwar = [System.Collections.Generic.List[int]]::new()
        foreach ($verse in $page.verses) {
            if ([int]$verse.verse_number -eq 1) {
                $key = [string]$verse.verse_key
                if ($key -and $key.Contains(":")) {
                    $suwar.Add([int]($key.Split(":")[0]))
                }
            }
            foreach ($w in ($verse.words | Where-Object { $_.char_type_name -eq "word" -or $_.char_type_name -eq "end" })) {
                $ln = [int]$w.line_number
                if ($ln -lt 1) { $ln = 1 }
                $entry = @($verse.verse_number, ($(if ($w.char_type_name -eq "end") { "*" } else { [string]$w.text_uthmani })))
                if (-not $lines.ContainsKey($ln)) { $lines[$ln] = [System.Collections.Generic.List[object]]::new() }
                [void]$lines[$ln].Add($entry)
            }
        }
        if ($lines.Count -eq 0) { throw "page has no word data" }
        $maxLine = ($lines.Keys | Measure-Object -Maximum).Maximum
        if ($maxLine -gt $maxLineGlobal) { $maxLineGlobal = $maxLine }
        # Serialize as {"l":[slot1Entries... , slot N ...]} slots 1..maxLine (empty slots = [])
        $sb = [System.Text.StringBuilder]::new()
        [void]$sb.Append('{"l":[')
        for ($i = 1; $i -le $maxLine; $i++) {
            if ($i -gt 1) { [void]$sb.Append(',') }
            if ($lines.ContainsKey($i)) {
                [void]$sb.Append('[')
                $first = $true
                foreach ($e in $lines[$i]) {
                    if (-not $first) { [void]$sb.Append(',') }
                    $first = $false
                    $t = ([string]$e[1]).Replace('\','\\').Replace('"','\"')
                    [void]$sb.Append('[').Append($e[0]).Append(',"').Append($t).Append('"]')
                }
                [void]$sb.Append(']')
            } else {
                [void]$sb.Append('[]')
            }
        }
        [void]$sb.Append(']')
        if ($suwar.Count -gt 0) {
            [void]$sb.Append(',"sw":[').Append(($suwar -join ',')).Append(']')
        }
        [void]$sb.Append('}')
        $json = $sb.ToString()
        [System.IO.File]::WriteAllText($file, $json, $utf8)
        $ok++
        if ($p % 40 -eq 0) { Write-Host "progress: $p pages done" }
        Start-Sleep -Milliseconds 120
    } catch {
        Write-Host "FAILED page $p : $($_.Exception.Message)"
        $fail += $p
    }
}
Write-Host "done ok=$ok failed=[$($fail -join ',')] maxLine=$maxLineGlobal"
