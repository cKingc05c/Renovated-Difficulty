# --- Paths ---
$projectDir = "F:\Chris\Minecraft\Renovatio\Renovated Difficulty"
$libsDir    = "$projectDir\build\libs"
$oldModsDir = "$projectDir\oldmods"

$modsDir    = "C:\Users\Chris\AppData\Roaming\PrismLauncher\instances\Renovatio 1.21.1\.minecraft\mods"
$gradleFile = "$projectDir\gradle.properties"

$modNamePrefix = "Renovated_Difficulty"

# --- Ensure folders exist ---
if (!(Test-Path $oldModsDir)) { New-Item -ItemType Directory -Path $oldModsDir | Out-Null }

# --- 1. Get newest non-sources jar ---
$newJar = Get-ChildItem $libsDir -Filter "*.jar" |
        Where-Object { $_.Name -notmatch "sources" } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

if (-not $newJar) {
    Write-Host "No jar found."
    exit
}

Write-Host "Newest jar:" $newJar.Name

# --- 2. Move current mod from mods -> oldmods ---
$existingMods = Get-ChildItem $modsDir -Filter "$modNamePrefix*.jar"

foreach ($mod in $existingMods) {
    Write-Host "Archiving old mod:" $mod.Name
    Move-Item $mod.FullName "$oldModsDir\$($mod.Name)" -Force
}

# --- 3. Move new jar into mods ---
Write-Host "Deploying new mod..."
Move-Item $newJar.FullName "$modsDir\$($newJar.Name)" -Force

# --- 4. Increment mod_version in gradle.properties ---
$content = Get-Content $gradleFile

for ($i = 0; $i -lt $content.Length; $i++) {

    if ($content[$i] -match "^mod_version=(.+)$") {

        $version = $Matches[1]
        $parts = $version.Split(".")

        $lastIndex = $parts.Length - 1
        $parts[$lastIndex] = [int]$parts[$lastIndex] + 1

        $newVersion = $parts -join "."

        $content[$i] = "mod_version=$newVersion"

        Write-Host "Version updated:" $version "->" $newVersion
    }
}

Set-Content $gradleFile $content

Write-Host "Done."