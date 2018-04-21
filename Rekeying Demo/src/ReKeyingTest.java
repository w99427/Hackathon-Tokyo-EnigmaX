import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import nics.crypto.Tuple;
import nics.crypto.proxy.afgh.AFGHGlobalParameters;
import nics.crypto.proxy.afgh.AFGHProxyReEncryption;

public class ReKeyingTest {

	static long cpuTime;
	static long time[] = new long[40];
	static int i = 0;

	public static void main(String[] args) {
		System.out.print("Starting...");
		//System.out.print(args[0]);
		//System.out.println(". How are you?");

		// 80 bits seg: r = 160, q = 512
		// 128 bits seg: r = 256, q = 1536
		// 256 bits seg: r = 512, q = 7680

		int rBits = 256; // 160; // 20 bytes
		int qBits = 1536; // 512; // 64 bytes

		AFGHGlobalParameters global = new AFGHGlobalParameters(rBits, qBits);

		Element sk_a = AFGHProxyReEncryption.generateSecretKey(global);
		medirTiempoMicroSegundos();
		System.out.println("GenerateSecrectKey for A...in " + Long.toString(time[i-1]) + " microSeconds\n");

		
		Element sk_b = AFGHProxyReEncryption.generateSecretKey(global);
		medirTiempoMicroSegundos();

		System.out.println("GenerateSecrectKey for B...in " + Long.toString(time[i-1]) + " microSeconds\n");

		Element sk_b_inverse = sk_b.invert();

		medirTiempoMicroSegundos();
		System.out.println("Calculate Inverse B...in " + Long.toString(time[i-1]) + " microSeconds\n");
		
		

		// Public keys

		Element pk_a = AFGHProxyReEncryption.generatePublicKey(sk_a, global);
		medirTiempoMicroSegundos();
		System.out.println("Generate Public Key for A...in " + Long.toString(time[i-1]) + " microSeconds\n");

		Element pk_b = AFGHProxyReEncryption.generatePublicKey(sk_b, global);
		medirTiempoMicroSegundos();
		System.out.println("Generate Public Key for B...in " + Long.toString(time[i-1]) + " microSeconds\n");

		ElementPowPreProcessing pk_a_ppp = pk_a.pow();

		medirTiempoMicroSegundos();
		System.out.println("Pre Processing pow for A...in " + Long.toString(time[i-1]) + " microSeconds\n");

		// Re-Encryption Key

		Element rk_a_b = AFGHProxyReEncryption.generateReEncryptionKey(pk_b, sk_a);
		medirTiempoMicroSegundos();
		System.out.println("Generate Re-Enc Key...in " + Long.toString(time[i-1]) + " microSeconds\n");

		String message = "12345678901234567890123456789012";
		
		
		Element m = AFGHProxyReEncryption.stringToElement(message, global.getG2());
		medirTiempoMicroSegundos();
		System.out.println("Processing Message...in " + Long.toString(time[i-1]) + " microSeconds\n");

		Tuple c_a = AFGHProxyReEncryption.secondLevelEncryption(m, pk_a_ppp, global);
		medirTiempoMicroSegundos();
		System.out.println("Processing SecondLevel Enc...in " + Long.toString(time[i-1]) + " microSeconds\n");

		PairingPreProcessing e_ppp = global.getE().pairing(rk_a_b);
		medirTiempoMicroSegundos();
		System.out.println("Pairing a and b ...in " + Long.toString(time[i-1]) + " microSeconds\n");

		Tuple c_b = AFGHProxyReEncryption.reEncryption(c_a, rk_a_b, e_ppp);
		medirTiempoMicroSegundos();
		System.out.println("Rekeying ...in " + Long.toString(time[i-1]) + " microSeconds\n");
		

		Element m2 = AFGHProxyReEncryption.firstLevelDecryptionPreProcessing(c_b, sk_b_inverse, global);
		medirTiempoMicroSegundos();
		System.out.println("FirstLevel Dec ...in " + Long.toString(time[i-1]) + " microSeconds\n");


		assert message.equals(new String(m2.toBytes()).trim());

/*		for (int j = 0; j < i; j++) {
			System.out.println(time[j]);
		}*/
	}

	public static long medirTiempoMicroSegundos() {
		time[i] = (System.nanoTime() - cpuTime) / 1000;
		i++;
		cpuTime = System.nanoTime();
		return time[i];
	}

}
