package rita.net;

/**
 * Protocolo con el que se comunican las instancias de RITA. ADD: pide que se
 * sume el robot que se está enviando (desencadena un UPDATE por parte del
 * receptor para actualizar al nuevo con los robot que ya estaban en el grupo)
 * REMOVE: eliminar al robot del grupo UPDATE: solo actualizar los archivos, no
 * desencadena otra accion
 * */
public enum Protocol {

	ADD("add"), REMOVE("remove"), UPDATE("update");

	String protText;

	Protocol(String protText) {
		this.protText = protText;
	}

	public String getText() {
		return this.protText;
	}

	public static Protocol getByValue(String value) {
		for (Protocol p : Protocol.values()) {
			if (p.getText().equals(value))
				return p;

		}
		return null;
	}

}
