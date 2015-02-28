$ErrorActionPreference = "Stop"

if ([environment]::Is64BitOperatingSystem) {
    $arch = "x86_64"
    $install_dir = "C:\Program Files\Rust"
} else {
    $arch = "i686"
    $install_dir = "C:\Program Files (x86)\Rust"
}

$package = "rust-nightly-$($arch)-pc-windows-gnu.exe"
$url = "https://static.rust-lang.org/dist/$($package)"

echo "Downloading Rust and Cargo from $($url)"
Start-FileDownload $url

echo "Installing Rust"
#$install_args = "/VERYSILENT", "/NORESTART", "/DIR='$($install_dir)'"
Start-Process $package -Wait
echo "Looking for rustc.exe"
Get-Command "rustc.exe" | Select-Object -ExpandProperty Definition
echo "Path"
echo $env:Path
echo "refreshing Path"
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "User")
$env:Path += ";" + [System.Environment]::GetEnvironmentVariable("Path", "Machine")
echo "Path"
echo $env:Path

#echo "Adding $($install_dir)\bin to the path"
#[Environment]::SetEnvironmentVariable("Path", [System.Environment]::GetEnvironmentVariable("Path", "User") + "$($install_dir)\bin", "User")

echo "C:\"
ls "C:\"

echo "C:\Program Files"
ls "C:\Program Files"

echo "C:\Program Files (x86)"
ls "C:\Program Files (x86)"

echo "Rust and Cargo are ready to roll!"
