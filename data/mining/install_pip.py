import os
import sys
import urllib.request

def install_pip():
    print("Descargando get-pip.py...")
    url = "https://bootstrap.pypa.io/get-pip.py"
    urllib.request.urlretrieve(url, "get-pip.py")
    
    print("Instalando pip...")
    os.system(f"{sys.executable} get-pip.py")
    
    print("Limpieza...")
    if os.path.exists("get-pip.py"):
        os.remove("get-pip.py")
    
    print("Pip instalado. Ahora puedes instalar paquetes con:")
    print(f"{sys.executable} -m pip install <nombre-del-paquete>")

if __name__ == "__main__":
    install_pip() 