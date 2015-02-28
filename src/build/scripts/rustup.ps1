# Licensed under the Apache License, Version 2.0 <LICENSE-APACHE or
# http://www.apache.org/licenses/LICENSE-2.0> or the MIT license
# <LICENSE-MIT or http://opensource.org/licenses/MIT>, at your
# option. This file may not be copied, modified, or distributed
# except according to those terms.

function Expand-ZIPFile($file, $destination) {
    $shell = new-object -com shell.application
    $zip = $shell.namespace($file)

    if (-Not (Test-Path "$destination")) {
        New-Item $destination -ItemType Directory -Force
    }

    $dst = $shell.namespace($destination)
    $dst.Copyhere($zip.items())
}

function Acquire-7z() {
    $7z_path = ""

    # Check for user installation, then machine installation.
    if (Test-Path -Path HKCU:\Software\7-Zip) {
        $7z_path = (Get-ItemProperty -Path HKCU:\Software\7-Zip).Path
    } elseif (Test-Path -Path HKLM:\Software\7-Zip) {
        $7z_path = (Get-ItemProperty -Path HKLM:\Software\7-Zip).Path
    }

    # Ensure the path from the registry is valid.
    if ($7z_path -and (Test-Path (Join-Path $7z_path "7z.exe"))) {
        $7z_path = Join-Path $7z_path "7z.exe"
        Write-Host "Using local 7-Zip at $7z_path"
    } else {
        $7z_url = "http://downloads.sourceforge.net/project/sevenzip/7-Zip/9.20/7za920.zip"
        $7z_tmp = "$TMP_DIR\7za920.zip"
        $7z_ex = "$TMP_DIR\7za.exe"

        # Did we already download and extract it?
        if (!(Test-Path $7z_ex)) {
            # Download 7zip
            Invoke-WebRequest $7z_url -OutFile $7z_tmp
            Expand-ZIPFile -File $7z_tmp -Destination "$TMP_DIR"
        }

        $7z_path = $7z_ex
        Write-Host "Using downloaded 7-Zip at $7z_path"
    }

    $7z_path
}

function which($name) {
    Get-Command $name | Select-Object -ExpandProperty Definition
}

$TMP_DIR = "$env:temp\rustup-tmp-install"
New-Item $TMP_DIR -ItemType Directory -Force | Out-Null
Set-Location $TMP_DIR

# Detect 32 or 64 bit
switch ([IntPtr]::Size) {
    4 {
        $arch = 32
        $rust_dl = "https://static.rust-lang.org/dist/rust-nightly-i686-pc-windows-gnu.exe"
        $cargo_dl = "https://static.rust-lang.org/cargo-dist/cargo-nightly-i686-pc-windows-gnu.tar.gz"
    }
    8 {
        $arch = 64
        $rust_dl = "https://static.rust-lang.org/dist/rust-nightly-x86_64-pc-windows-gnu.exe"
        $cargo_dl = "https://static.rust-lang.org/cargo-dist/cargo-nightly-x86_64-pc-windows-gnu.tar.gz"
    }
    default { echo "ERROR: The processor architecture could not be determined."; exit 1 }
}

$7z = Acquire-7z # Check/Download 7-Zip

# Download the latest rust and cargo binaries
$rust_installer = "$TMP_DIR\rust_install.exe"
$cargo_binary = "$TMP_DIR\cargo_install.tar.gz"

$web_client = new-object System.Net.WebClient
echo "Downloading the lastest Rust nightly - this may take a while"
$web_client.DownloadFile($rust_dl, $rust_installer)

echo "Downloading the latest Cargo nightly - this may take a while"
$web_client.DownloadFile($cargo_dl, $cargo_binary)

echo "Downloads complete."

# Install the rust binaries
echo "Installing Rust"
Start-Process $rust_installer -Wait

# Refresh path for this process after installation
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "User")
$env:Path += ";" + [System.Environment]::GetEnvironmentVariable("Path", "Machine")

# Looking for the dir which has rustc in it, which may fail if the user doesn't add rust\bin to
# their path or for multiple rust versions
$rust_bin = which "rustc.exe" | Split-Path
rustc --version
echo "Rust is Ready!"


# Extract the Cargo binary with 7-Zip
echo "Installing Cargo"
Start-Process $7z -ArgumentList "e $cargo_binary -y" -NoNewWindow -Wait
Start-Process $7z -ArgumentList "e .\cargo_install.tar *.exe -r -y" -NoNewWindow -Wait

try {
    # Attempt to copy Cargo next to rustc.  If this fails, it's because the user installed Rust to a privileged path.
    Copy-Item "$TMP_DIR\cargo.exe" $rust_bin -ErrorAction Stop
} catch {
    # Unprivileged copy failed, so do a privileged copy instead.  This will perform a UAC prompt.
    # This is unfortunately the cleanest way of achieving this.
    # Gotcha #1: '-Verb RunAs' and '-NoNewWindow' are incompatible, so instead we use '-WindowStyle Hidden'
    $proc = Start-Process powershell -ArgumentList "-Command &{Copy-Item \`"$TMP_DIR\cargo.exe\`" \`"$rust_bin\`"}" -Verb RunAs -WindowStyle Hidden -PassThru

    # Gotcha #2: '-Verb RunAs' is ALSO incompatible with "-Wait"... yuck.
    # Spin for a bit so we don't try to cargo -V until it has actually been copied.
    do { Start-Sleep -m 50 } until ($proc.HasExited)
}

cargo --version
echo "Cargo is Ready!"

