$ErrorActionPreference = "Stop"

$install_dir = "C:\Rust"
$arch = @{$true = "x86_64"; $false = "i686"}[[environment]::Is64BitOperatingSystem]
$package = "rust-nightly-$($arch)-pc-windows-gnu.exe"
$url = "https://static.rust-lang.org/dist/$($package)"

echo "Downloading Rust and Cargo from $($url)"
Start-FileDownload $url

echo "Installing Rust into $($install_dir)"
& ".\$($package)" /VERYSILENT /NORESTART /DIR=$install_dir

echo "Adding Rust to the machine path"
[Environment]::SetEnvironmentVariable("Path", [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";$($install_dir)\bin", [System.EnvironmentVariableTarget]::Machine)

echo "Rust and Cargo are ready to roll!"
