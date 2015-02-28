$ErrorActionPreference = "Stop"

$install_dir = "C:\Rust"
$arch = @{$true = "x86_64"; $false = "i686"}[[environment]::Is64BitOperatingSystem]
$package = "rust-nightly-$($arch)-pc-windows-gnu.exe"
$url = "https://static.rust-lang.org/dist/$($package)"

echo "Downloading Rust and Cargo from $($url)"
Start-FileDownload $url

echo "Installing Rust into $($install_dir)"
& ".\$($package)" /VERYSILENT /NORESTART /DIR=$install_dir

echo "Adding $($install_dir)\bin to the path"
[Environment]::SetEnvironmentVariable("Path", [System.Environment]::GetEnvironmentVariable("Path", "User") + "$($install_dir)\bin", "User")

echo "C:\"
ls "C:\"

echo "C:\Program Files"
ls "C:\Program Files"

echo "C:\Program Files (x86)"
ls "C:\Program Files (x86)"

echo "Rust and Cargo are ready to roll!"
