# EduConnectB


Sigue este gist para encontrar el dump de la BD.

https://gist.github.com/HcVm/28c46ca6f7815ccfb41556405705c3d4

**Estamos usando Jitsi para la intergracion de videollamadas, puedes consultar la documentación para su implementación en:

https://jitsi.github.io/handbook/docs/intro

o Te dejo un ejemplo de integración:

--Integrar Jitsi Meet en el frontend (React):

--Instala la biblioteca lib-jitsi-meet:

***npm install lib-jitsi-meet


---Crea un componente React para mostrar la videollamada. Puedes utilizar el siguiente ejemplo como base:
**JavaScript
--------------------------------------------------------------------------------------------------------------
import React, { useState, useEffect } from 'react';
import JitsiMeetExternalAPI from 'lib-jitsi-meet';

function VideoLlamada({ salaJitsi }) {
  const [api, setApi] = useState(null);

  useEffect(() => {
    const domain = 'meet.jit.si'; // O la URL de tu servidor Jitsi (Actualmente estamos usando el dominio, no un servidor local)
    const options = {
      roomName: salaJitsi,
      width: '100%',
      height: '500px',
      parentNode: document.querySelector('#meet'),
      configOverwrite: {
        // Puedes personalizar la configuración de Jitsi aquí
      },
      interfaceConfigOverwrite: {
        // Puedes personalizar la interfaz de Jitsi aquí
      },
    };

    const newApi = new JitsiMeetExternalAPI(domain, options);
    setApi(newApi);

    return () => {
      if (api) {
        api.dispose();
      }
    };
  }, [salaJitsi]); // Asegúrate de que el componente se vuelva a renderizar cuando cambie 'salaJitsi'

  return <div id="meet"></div>; // Contenedor para la videollamada
}

export default VideoLlamada;

-----------------------------------------------------------------------------------------------------------