# 🤖 Agente PC — IA que controla tu computadora

Un agente que usa Claude para ver y controlar tu pantalla en lenguaje natural.

---

## ⚙️ Instalación (1 sola vez)

Abre PowerShell o la terminal de VS Code y ejecuta:

```powershell
pip install anthropic pyautogui pillow
```

---

## 🔑 Configurar tu API Key

Tienes dos opciones:

**Opción A — Editar el archivo (más fácil):**
Abre `agente.py`, busca esta línea y pon tu key:
```python
API_KEY = "sk-ant-aqui-va-tu-key"
```

**Opción B — Variable de entorno (más seguro):**
```powershell
$env:ANTHROPIC_API_KEY = "sk-ant-aqui-va-tu-key"
```

> Obtén tu API key en: https://console.anthropic.com/

---

## ▶️ Cómo ejecutarlo

```powershell
python agente.py
```

Luego escribe tareas en lenguaje natural:
```
>> Abre el bloc de notas y escribe "Hola Mundo"
>> Abre la calculadora y calcula 999 x 12
>> Busca en Google el clima de Santiago
>> q
```

---

## 🛑 Cómo detenerlo si se descontrola

Mueve el mouse rápidamente a la **esquina superior izquierda** de la pantalla.  
Esto activa el "failsafe" de pyautogui y detiene el agente inmediatamente.

---

## 📁 Archivos del proyecto

```
agente_pc/
├── agente.py     ← Código principal del agente
└── README.md     ← Este archivo
```

---

## ❓ Preguntas frecuentes

**¿Cuánto cuesta?**  
Cada tarea consume ~1.000–5.000 tokens. Con el plan gratuito de la API tienes créditos de prueba.

**¿Funciona con cualquier pantalla?**  
Sí. El agente captura tu pantalla en tiempo real, sin importar resolución.

**¿Es seguro?**  
El agente tiene acceso completo a tu PC. Úsalo solo con tareas que tú mismo harías.
