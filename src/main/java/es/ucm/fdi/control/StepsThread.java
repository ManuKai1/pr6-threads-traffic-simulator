package es.ucm.fdi.control;

/**
 * Clase que comienza un hilo para ejecutar
 * pasos con un determinado delay.
 * Creada en base a implementación del profesor
 * en clase.
 */
public class StepsThread {
	
	//A ejecutar antes de comenzar
	private Runnable before;
	
	//A ejecutar en el proceso
	private Runnable during;
	
	//A ejecutar al parar
	private Runnable after;
	
	//Si nos han pedido parar
	private boolean stopRequested = false;
	
	//Número de pasos a ejecutar
	private int steps;
	
	/**
	 * Constructora del hilo según runnables.
	 * @param before : a ejecutar antes
	 * @param during : a ejecutar durante
	 * @param after : a ejecutar después
	 */
	public StepsThread(Runnable before, Runnable during, Runnable after) {
		this.before = before;
		this.during = during;
		this.after = after;
	}
	
	/**
	 * Comienza la ejecución del hilo.
	 * @param steps : numero de pasos a ejecutar
	 * @param delay : retardo entre pasos.
	 * @return Thread ejecutado.
	 */
	public Thread start(int steps, int delay){
		this.steps = steps;
		this.stopRequested = false;
		
		Thread t = new Thread(()-> {
		try {
			before.run();
			while(!stopRequested && StepsThread.this.steps > 0){
				during.run();
				try {
					Thread.sleep(delay);
				}
				catch(InterruptedException ie){
					
				}
				StepsThread.this.steps --;
			}
		} finally{
			after.run();
		}
		});
		t.start();
		return t;
	}
	
	/**
	 * Detiene el hilo.
	 */
	public void stop(){
		stopRequested = true;
	}
	
	/**
	 * Getter de steps.
	 * @return número de pasos que faltan.
	 */
	public int getSteps(){
		return steps;
	}
	
}
