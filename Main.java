package moi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Main {

	/*
	 * Les 2 int qui suivent sont les codes qui servent à identifier l'appui sur une
	 * note et la relâche de cette note dans un fichier midi.
	 */

	/** 0x90 en hexadécimal = 144 en décimal */
	public static final int NOTE_ON = 0x90;
	/** 0x80 en hexadécimal = 128 en décimal */
	public static final int NOTE_OFF = 0x80;

	private static final List<String> listeModesMajeur;
	private static final List<String> listeModesMineur;
	private static final Map<String, Integer> mapNoteNumero;

	/*
	 * Maps qui servent à l'identification de la tonalité (majeure ou mineure), s'il
	 * y en a une.
	 */
	private static final Map<String, Double> mapMajorProfile;
	private static final Map<String, Double> mapMinorProfile;

	private static Integer nombreNotesTotal = 0;

	static {
		listeModesMajeur = Arrays.asList("CM", "C#M", "DM", "D#M", "EM", "FM", "F#M", "GM", "G#M", "AM", "A#M", "BM");
		listeModesMineur = Arrays.asList("Cm", "C#m", "Dm", "D#m", "Em", "Fm", "F#m", "Gm", "G#m", "Am", "A#m", "Bm");

		Map<String, Integer> map1 = new LinkedHashMap<String, Integer>();
		map1.put("C", 0);
		map1.put("C#", 1);
		map1.put("D", 2);
		map1.put("D#", 3);
		map1.put("E", 4);
		map1.put("F", 5);
		map1.put("F#", 6);
		map1.put("G", 7);
		map1.put("G#", 8);
		map1.put("A", 9);
		map1.put("A#", 10);
		map1.put("B", 11);
		mapNoteNumero = Collections.unmodifiableMap(map1);

		Map<String, Double> map2 = new LinkedHashMap<String, Double>();
		map2.put("do", 6.35);
		map2.put("do#", 2.23);
		map2.put("re", 3.48);
		map2.put("re#", 2.33);
		map2.put("mi", 4.38);
		map2.put("fa", 4.09);
		map2.put("fa#", 2.52);
		map2.put("so", 5.19);
		map2.put("so#", 2.39);
		map2.put("la", 3.66);
		map2.put("la#", 2.29);
		map2.put("ti", 2.88);
		mapMajorProfile = Collections.unmodifiableMap(map2);

		Map<String, Double> map3 = new LinkedHashMap<String, Double>();
		map3.put("la", 6.33);
		map3.put("la#", 2.69);
		map3.put("ti", 3.52);
		map3.put("do", 5.38);
		map3.put("do#", 2.60);
		map3.put("re", 3.53);
		map3.put("re#", 2.54);
		map3.put("mi", 4.75);
		map3.put("fa", 3.98);
		map3.put("fa#", 2.69);
		map3.put("so", 3.34);
		map3.put("so#", 3.17);
		mapMinorProfile = Collections.unmodifiableMap(map3);

		/*
		 * Création de la map qui à chaque note en associe une autre. Va de C0 à B9
		 * compris. Inutile pour l'instant mais peut servir plus tard, si on veut faire
		 * d'autres transformations que de l'harmonie négative.
		 */
		Map<String, String> mapNotesExhaustive = new HashMap<String, String>();
		for (int i = 0; i < 10; i++) {
			for (String uneNote : mapNoteNumero.keySet()) {
				/* Pour l'initialiser, on ne fait que du x->x */
				mapNotesExhaustive.put(uneNote + i, uneNote + i);
			}
		}

		mapNotesExhaustive.put("C0", "C0");
		mapNotesExhaustive.put("C#0", "C#0");
		mapNotesExhaustive.put("D0", "D0");
		mapNotesExhaustive.put("D#0", "D#0");
		mapNotesExhaustive.put("E0", "E0");
		mapNotesExhaustive.put("F0", "F0");
		mapNotesExhaustive.put("F#0", "F#0");
		mapNotesExhaustive.put("G0", "G0");
		mapNotesExhaustive.put("G#0", "G#0");
		mapNotesExhaustive.put("A0", "A0");
		mapNotesExhaustive.put("A#0", "A#0");
		mapNotesExhaustive.put("B0", "B0");
		mapNotesExhaustive.put("C1", "C1");
		mapNotesExhaustive.put("C#1", "C#1");
		mapNotesExhaustive.put("D1", "D1");
		mapNotesExhaustive.put("D#1", "D#1");
		mapNotesExhaustive.put("E1", "E1");
		mapNotesExhaustive.put("F1", "F1");
		mapNotesExhaustive.put("F#1", "F#1");
		mapNotesExhaustive.put("G1", "G1");
		mapNotesExhaustive.put("G#1", "G#1");
		mapNotesExhaustive.put("A1", "A1");
		mapNotesExhaustive.put("A#1", "A#1");
		mapNotesExhaustive.put("B1", "B1");
		mapNotesExhaustive.put("C2", "C2");
		mapNotesExhaustive.put("C#2", "C#2");
		mapNotesExhaustive.put("D2", "D2");
		mapNotesExhaustive.put("D#2", "D#2");
		mapNotesExhaustive.put("E2", "E2");
		mapNotesExhaustive.put("F2", "F2");
		mapNotesExhaustive.put("F#2", "F#2");
		mapNotesExhaustive.put("G2", "G2");
		mapNotesExhaustive.put("G#2", "G#2");
		mapNotesExhaustive.put("A2", "A2");
		mapNotesExhaustive.put("A#2", "A#2");
		mapNotesExhaustive.put("B2", "B2");
		mapNotesExhaustive.put("C3", "C3");
		mapNotesExhaustive.put("C#3", "C#3");
		mapNotesExhaustive.put("D3", "D3");
		mapNotesExhaustive.put("D#3", "D#3");
		mapNotesExhaustive.put("E3", "E3");
		mapNotesExhaustive.put("F3", "F3");
		mapNotesExhaustive.put("F#3", "F#3");
		mapNotesExhaustive.put("G3", "G3");
		mapNotesExhaustive.put("G#3", "G#3");
		mapNotesExhaustive.put("A3", "A3");
		mapNotesExhaustive.put("A#3", "A#3");
		mapNotesExhaustive.put("B3", "B3");
		mapNotesExhaustive.put("C4", "C4");
		mapNotesExhaustive.put("C#4", "C#4");
		mapNotesExhaustive.put("D4", "D4");
		mapNotesExhaustive.put("D#4", "D#4");
		mapNotesExhaustive.put("E4", "E4");
		mapNotesExhaustive.put("F4", "F4");
		mapNotesExhaustive.put("F#4", "F#4");
		mapNotesExhaustive.put("G4", "G4");
		mapNotesExhaustive.put("G#4", "G#4");
		mapNotesExhaustive.put("A4", "A4");
		mapNotesExhaustive.put("A#4", "A#4");
		mapNotesExhaustive.put("B4", "B4");
		mapNotesExhaustive.put("C5", "C5");
		mapNotesExhaustive.put("C#5", "C#5");
		mapNotesExhaustive.put("D5", "D5");
		mapNotesExhaustive.put("D#5", "D#5");
		mapNotesExhaustive.put("E5", "E5");
		mapNotesExhaustive.put("F5", "F5");
		mapNotesExhaustive.put("F#5", "F#5");
		mapNotesExhaustive.put("G5", "G5");
		mapNotesExhaustive.put("G#5", "G#5");
		mapNotesExhaustive.put("A5", "A5");
		mapNotesExhaustive.put("A#5", "A#5");
		mapNotesExhaustive.put("B5", "B5");
		mapNotesExhaustive.put("C6", "C6");
		mapNotesExhaustive.put("C#6", "C#6");
		mapNotesExhaustive.put("D6", "D6");
		mapNotesExhaustive.put("D#6", "D#6");
		mapNotesExhaustive.put("E6", "E6");
		mapNotesExhaustive.put("F6", "F6");
		mapNotesExhaustive.put("F#6", "F#6");
		mapNotesExhaustive.put("G6", "G6");
		mapNotesExhaustive.put("G#6", "G#6");
		mapNotesExhaustive.put("A6", "A6");
		mapNotesExhaustive.put("A#6", "A#6");
		mapNotesExhaustive.put("B6", "B6");
		mapNotesExhaustive.put("C7", "C7");
		mapNotesExhaustive.put("C#7", "C#7");
		mapNotesExhaustive.put("D7", "D7");
		mapNotesExhaustive.put("D#7", "D#7");
		mapNotesExhaustive.put("E7", "E7");
		mapNotesExhaustive.put("F7", "F7");
		mapNotesExhaustive.put("F#7", "F#7");
		mapNotesExhaustive.put("G7", "G7");
		mapNotesExhaustive.put("G#7", "G#7");
		mapNotesExhaustive.put("A7", "A7");
		mapNotesExhaustive.put("A#7", "A#7");
		mapNotesExhaustive.put("B7", "B7");
		mapNotesExhaustive.put("C8", "C8");
		mapNotesExhaustive.put("C#8", "C#8");
		mapNotesExhaustive.put("D8", "D8");
		mapNotesExhaustive.put("D#8", "D#8");
		mapNotesExhaustive.put("E8", "E8");
		mapNotesExhaustive.put("F8", "F8");
		mapNotesExhaustive.put("F#8", "F#8");
		mapNotesExhaustive.put("G8", "G8");
		mapNotesExhaustive.put("G#8", "G#8");
		mapNotesExhaustive.put("A8", "A8");
		mapNotesExhaustive.put("A#8", "A#8");
		mapNotesExhaustive.put("B8", "B8");
		mapNotesExhaustive.put("C9", "C9");
		mapNotesExhaustive.put("C#9", "C#9");
		mapNotesExhaustive.put("D9", "D9");
		mapNotesExhaustive.put("D#9", "D#9");
		mapNotesExhaustive.put("E9", "E9");
		mapNotesExhaustive.put("F9", "F9");
		mapNotesExhaustive.put("F#9", "F#9");
		mapNotesExhaustive.put("G9", "G9");
		mapNotesExhaustive.put("G#9", "G#9");
		mapNotesExhaustive.put("A9", "A9");
		mapNotesExhaustive.put("A#9", "A#9");
		mapNotesExhaustive.put("B9", "B9");
		mapNotesExhaustive.put("C10", "C10");
		mapNotesExhaustive.put("C#10", "C#10");
		mapNotesExhaustive.put("D10", "D10");
		mapNotesExhaustive.put("D#10", "D#10");
		mapNotesExhaustive.put("E10", "E10");
		mapNotesExhaustive.put("F10", "F10");
		mapNotesExhaustive.put("F#10", "F#10");
		mapNotesExhaustive.put("G10", "G10");
		mapNotesExhaustive.put("G#10", "G#10");
		mapNotesExhaustive.put("A10", "A10");
		mapNotesExhaustive.put("A#10", "A#10");
		mapNotesExhaustive.put("B10", "B10");
	}

	/**
	 * TODO Otto 30/10/2018 mettre les paramètres en entrée du programme
	 */
	public static void main(String[] args) {

		/* Chemin d'accès au fichier */
		String cheminIn = "C:\\Users\\...";

		File dir = new File(cheminIn);

		/*
		 * Peut servir pour convertir plusieurs fichiers à la fois, dans ce cas,
		 * décommenter. TODO Otto 30/10/2018 faire quelque chose de plus propre
		 */
		File[] directoryListing = dir.listFiles();
//		for (File child : directoryListing) {

		String nomFichierIn = "for_elise_by_beethoven";
		String cheminOut = "C:\\Users\\othman\\OneDrive\\Documents\\eclipse-workspace\\negative\\resources\\negatif\\";
		String nomFichierOut = nomFichierIn + "_negative";

		String tonalite = "A";

		/* 3 méthodes : plusProche, octave, centrale */
		String methode = "centrale";

		Sequence sequenceIn = null;
		try {
			sequenceIn = MidiSystem.getSequence(new File(cheminIn + nomFichierIn + ".mid"));

			/* Logs exhaustives des notes avant conversion */
			logsExhaustivesNotes(sequenceIn);

			/* Conversion */
			for (Track track : sequenceIn.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
							/* CONVERSION */
							conversion(methode, sm, tonalite);
						}
					}
				}
			}

			MidiSystem.write(sequenceIn, 1, new File(cheminOut + nomFichierOut + ".mid"));
		} catch (InvalidMidiDataException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		}
	}

	/**
	 * Calcule les coefficients de correlation pour chaque mode majeur et mineur de
	 * chaque clé. Cf http://rnhart.net/articles/key-finding/ (Krumhansl-Schmuckler
	 * key-finding algorithm)
	 * 
	 * @return Une Map du type CM->0.02, D#m->0.9, etc.
	 */
	public static Map<String, Double> calculerListeCoefficients(Map<String, Integer> mapCompteurNotes) {
		Map<String, Double> resultat = new HashMap<>();

		Map<String, String> mapTiSoValue = new HashMap<>();

		int i = 0;
		for (String unModeMaj : listeModesMajeur) {

			/* Nous donne la Map do->C, do#->C#, re->D, etc. */
			for (String unMajor : mapMajorProfile.keySet()) {

				mapTiSoValue.put(unMajor, getNomNoteSansOctaveFromNum(i));
				i++;
			}
			i++;
			ArrayList<Double> x = new ArrayList<>();
			ArrayList<Double> y = new ArrayList<>();
			for (String unMajor : mapMajorProfile.keySet()) {
				x.add(mapMajorProfile.get(unMajor));
				y.add(mapCompteurNotes.get(mapTiSoValue.get(unMajor)).doubleValue());
			}

			resultat.put(unModeMaj, calculerCoefficient(x, y));

		}

		i = 0;
		for (String unModeMin : listeModesMineur) {

			/* Nous donne la Map do->C, do#->C#, re->D, etc. */
			for (String unMinor : mapMinorProfile.keySet()) {
				mapTiSoValue.put(unMinor, getNomNoteSansOctaveFromNum(i));
				i++;
			}
			i++;
			ArrayList<Double> x = new ArrayList<>();
			ArrayList<Double> y = new ArrayList<>();
			for (String unMinor : mapMinorProfile.keySet()) {
				x.add(mapMinorProfile.get(unMinor));
				y.add(mapCompteurNotes.get(mapTiSoValue.get(unMinor)).doubleValue());
			}

			resultat.put(unModeMin, calculerCoefficient(x, y));

		}

		return resultat;
	}

	/**
	 * Cf http://rnhart.net/articles/key-finding/ (Krumhansl-Schmuckler key-finding
	 * algorithm)
	 */
	public static double calculerCoefficient(ArrayList<Double> x, ArrayList<Double> y) {

		double numerateur = 0.0;
		double denominateur = 0.0;
		double denominateurx = 0.0;
		double denominateury = 0.0;

		Double xaverage = x.stream().mapToDouble(val -> val).average().orElse(0.0);
		Double yaverage = y.stream().mapToDouble(val -> val).average().orElse(0.0);

		for (int i = 0; i < x.size(); i++) {
			numerateur = numerateur + (x.get(i) - xaverage) * (y.get(i) - yaverage);
		}

		for (int i = 0; i < x.size(); i++) {
			denominateurx = denominateurx + Math.pow(x.get(i) - xaverage, 2);
		}

		for (int i = 0; i < y.size(); i++) {
			denominateury = denominateury + Math.pow(y.get(i) - yaverage, 2);
		}

		denominateur = Math.sqrt(denominateurx * denominateury);

		return numerateur / denominateur;
	}

	/**
	 * Méthode centrale qui effectue la conversion
	 * 
	 * @param methode  : à choisir entre "symetrique", "plusProche", "octave", ou
	 *                 "centrale" (cf les commentaires méthodes Java pour
	 *                 explications)
	 * @param sm       : la note
	 * @param tonalite :
	 * @throws InvalidMidiDataException
	 */
	public static void conversion(String methode, ShortMessage sm, String tonalite) throws InvalidMidiDataException {
		if (methode.equals("symetrique"))
			/*
			 * sm.getData1() = numéro de la note (de 0 = C0 à 131 = B10, en convention midi)
			 * sm.getData2() = vélocité
			 */
			sm.setMessage(sm.getStatus(), negationSymetrique(sm.getData1(), tonalite), sm.getData2());
		else if (methode.equals("plusProche"))
			sm.setMessage(sm.getStatus(), negationPlusProche(sm.getData1(), tonalite), sm.getData2());
		else if (methode.equals("octave"))
			sm.setMessage(sm.getStatus(), negationMemeOctave(sm.getData1(), tonalite), sm.getData2());
		else if (methode.equals("centrale"))
			sm.setMessage(sm.getStatus(), negationCentrale(sm.getData1(), tonalite), sm.getData2());
	}

	/**
	 * Sert à écrire des infos sur les notes dans la console, si besoin, décommenter
	 * TODO Otto 30/10/2018 faire quelque chose de plus propre
	 */
	public static void logsExhaustivesNotes(Sequence sequenceIn) {

		Map<String, Integer> mapCompteurNotes = new HashMap<String, Integer>();
		mapCompteurNotes.putAll(mapNoteNumero);
		mapCompteurNotes.replaceAll((key, oldValue) -> 0);

		int trackNumber = 0;
		for (Track track : sequenceIn.getTracks()) {
			trackNumber++;
//			System.out.println("Track " + trackNumber + ": size = " + track.size());
//			System.out.println();
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
//				System.out.print("@" + event.getTick() + " ");
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
//					System.out.print("Channel: " + sm.getChannel() + " ");
					if (sm.getCommand() == NOTE_ON) {
						int key = sm.getData1();
						int velocity = sm.getData2();
//						System.out.println("Note on, " + getNomNoteAvecOctaveFromNum(key) + " key=" + key
//								+ " velocity: " + velocity);

						/* Comptage des notes */
						nombreNotesTotal++;
						String noteName = getNomNoteSansOctaveFromNum(key);
						mapCompteurNotes.put(noteName, mapCompteurNotes.get(noteName) + 1);
					} else if (sm.getCommand() == NOTE_OFF) {
						int key = sm.getData1();
						int velocity = sm.getData2();
//						System.out.println("Note off, " + getNomNoteAvecOctaveFromNum(key) + " key=" + key
//								+ " velocity: " + velocity);
					} else {
//						System.out.println("Command:" + sm.getCommand());
					}
				} else {
//					System.out.println("Other message: " + message.getClass());
				}
			}

//			System.out.println();
		}

		statsNotes(sequenceIn, mapCompteurNotes);

	}

	/**
	 * Sert à écrire quelques statistiques sur les notes du morceau, dont les
	 * calculs qui servent à trouver la tonalité.
	 */
	public static void statsNotes(Sequence sequenceIn, Map<String, Integer> mapCompteurNotes) {

		mapCompteurNotes = sortByValue(mapCompteurNotes);

		System.out.println("Nombre notes : " + nombreNotesTotal);
		System.out.println("Compteur notes : ");

		for (String uneNote : mapCompteurNotes.keySet()) {
			System.out.println(uneNote + " : " + mapCompteurNotes.get(uneNote));
		}

		System.out.println("Fréquence notes : ");

		for (String uneNote : mapCompteurNotes.keySet()) {
			System.out.println(uneNote + " : "
					+ Math.round((double) mapCompteurNotes.get(uneNote) * 100 / nombreNotesTotal) + " %");
		}

		/*
		 * Cf http://rnhart.net/articles/key-finding/ (Krumhansl-Schmuckler key-finding
		 * algorithm)
		 */
		Map<String, Double> mapCoefficients = sortByValue(calculerListeCoefficients(mapCompteurNotes));

		System.out.println("Coefficients correlation : ");
		for (String uneCle : mapCoefficients.keySet()) {
			System.out.println(uneCle + " : " + mapCoefficients.get(uneCle));
		}

	}

	/**
	 * Principe : on échange les notes entre elles par rapport à l'axe de symétrie
	 * constitué par IIb+II le plus proche.
	 */
	public static int negationSymetrique(int numeroNote, String tonique) {
		int numeroNoteDansOctave = numeroNote % 12;

		int numeroTonique = mapNoteNumero.get(tonique);

		/*
		 * L'idée est d'identifier la position de la note par rapport à la tonique, et
		 * de la transposer en fonction de cela.
		 */

		if (numeroNoteDansOctave == numeroTonique % 12)
			return numeroNote + 7; /* Correspond à la 5te juste supérieure. Exemple : C3->G3 */
		else if (numeroNoteDansOctave == (numeroTonique + 1) % 12)
			return numeroNote + 5; /* Correspond à la 4te juste supérieure. Exemple : C#3->F#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 2) % 12)
			return numeroNote + 3; /* Correspond à la 3rce mineure supérieure. Exemple : D3->F3 */
		else if (numeroNoteDansOctave == (numeroTonique + 3) % 12)
			return numeroNote + 1; /* Correspond à la 2nde mineure supérieure. Exemple : D#3->E3 */
		else if (numeroNoteDansOctave == (numeroTonique + 4) % 12)
			return numeroNote - 1; /* Correspond à la 2nde mineure inférieure. Exemple : E3->D#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 5) % 12)
			return numeroNote - 3; /* Correspond à la 3rce mineure inférieure. Exemple : F3->D3 */
		else if (numeroNoteDansOctave == (numeroTonique + 6) % 12)
			return numeroNote - 5; /* Correspond à la 4te juste inférieure. Exemple : F#3->C#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 7) % 12)
			return numeroNote - 7; /* Correspond à la 5te juste inférieure. Exemple : G3->C3 */
		else if (numeroNoteDansOctave == (numeroTonique + 8) % 12)
			return numeroNote - 9; /* Correspond à la 6te majeure inférieure. Exemple : G#3->B2 */
		else if (numeroNoteDansOctave == (numeroTonique + 9) % 12)
			return numeroNote - 11; /* Correspond à la 7eme majeure inférieure. Exemple : A3->A#2 */
		else if (numeroNoteDansOctave == (numeroTonique + 10) % 12)
			return numeroNote + 11; /* Correspond à la 7eme majeure supérieure. Exemple : A#3->A4 */
		else if (numeroNoteDansOctave == (numeroTonique + 11) % 12)
			return numeroNote + 9; /* Correspond à la 6te majeure supérieure. Exemple : B3->G#4 */

		return numeroNote;
	}

	/**
	 * Principe : on échange les notes entre elles par rapport à l'axe de symétrie
	 * constitué par IIb+II le plus proche, sans jamais dépasser le triton.
	 */
	public static int negationPlusProche(int numeroNote, String tonique) {
		int numeroNoteDansOctave = numeroNote % 12;

		int numeroTonique = mapNoteNumero.get(tonique);

		/*
		 * L'idée est d'identifier la position de la note par rapport à la tonique, et
		 * de la transposer en fonction de cela.
		 */

		if (numeroNoteDansOctave == numeroTonique % 12)
			return numeroNote - 5;
		else if (numeroNoteDansOctave == (numeroTonique + 1) % 12)
			return numeroNote + 5;
		else if (numeroNoteDansOctave == (numeroTonique + 2) % 12)
			return numeroNote + 3;
		else if (numeroNoteDansOctave == (numeroTonique + 3) % 12)
			return numeroNote + 1;
		else if (numeroNoteDansOctave == (numeroTonique + 4) % 12)
			return numeroNote - 1;
		else if (numeroNoteDansOctave == (numeroTonique + 5) % 12)
			return numeroNote - 3;
		else if (numeroNoteDansOctave == (numeroTonique + 6) % 12)
			return numeroNote - 5;
		else if (numeroNoteDansOctave == (numeroTonique + 7) % 12)
			return numeroNote + 5;
		else if (numeroNoteDansOctave == (numeroTonique + 8) % 12)
			return numeroNote + 3;
		else if (numeroNoteDansOctave == (numeroTonique + 9) % 12)
			return numeroNote + 1;
		else if (numeroNoteDansOctave == (numeroTonique + 10) % 12)
			return numeroNote - 1;
		else if (numeroNoteDansOctave == (numeroTonique + 11) % 12)
			return numeroNote - 3;

		return numeroNote;
	}

	/**
	 * Principe : on échange les notes entre elles par rapport à l'axe de symétrie
	 * constitué par IIb+II le plus proche, en restant dans la même octave.
	 */
	public static int negationMemeOctave(int numeroNote, String tonique) {
		int octaveInitial = numeroNote / 12;

		int numeroNoteDansOctave = numeroNote % 12;

		int numeroTonique = mapNoteNumero.get(tonique);

		/*
		 * L'idée est d'identifier la position de la note par rapport à la tonique, et
		 * de la transposer en fonction de cela.
		 */

		int resultat = numeroNote;

		if (numeroNoteDansOctave == numeroTonique % 12)
			resultat = numeroNote + 7; /* Correspond à la 5te juste supérieure. Exemple : C3->G3 */
		else if (numeroNoteDansOctave == (numeroTonique + 1) % 12)
			resultat = numeroNote + 5; /* Correspond à la 4te juste supérieure. Exemple : C#3->F#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 2) % 12)
			resultat = numeroNote + 3; /* Correspond à la 3rce mineure supérieure. Exemple : D3->F3 */
		else if (numeroNoteDansOctave == (numeroTonique + 3) % 12)
			resultat = numeroNote + 1; /* Correspond à la 2nde mineure supérieure. Exemple : D#3->E3 */
		else if (numeroNoteDansOctave == (numeroTonique + 4) % 12)
			resultat = numeroNote - 1; /* Correspond à la 2nde mineure inférieure. Exemple : E3->D#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 5) % 12)
			resultat = numeroNote - 3; /* Correspond à la 3rce mineure inférieure. Exemple : F3->D3 */
		else if (numeroNoteDansOctave == (numeroTonique + 6) % 12)
			resultat = numeroNote - 5; /* Correspond à la 4te juste inférieure. Exemple : F#3->C#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 7) % 12)
			resultat = numeroNote - 7; /* Correspond à la 5te juste inférieure. Exemple : G3->C3 */
		else if (numeroNoteDansOctave == (numeroTonique + 8) % 12)
			resultat = numeroNote - 9; /* Correspond à la 6te majeure inférieure. Exemple : G#3->B3 */
		else if (numeroNoteDansOctave == (numeroTonique + 9) % 12)
			resultat = numeroNote - 11; /* Correspond à la 7eme majeure inférieure. Exemple : A3->A#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 10) % 12)
			resultat = numeroNote + 11; /* Correspond à la 7eme majeure supérieure. Exemple : A#3->A3 */
		else if (numeroNoteDansOctave == (numeroTonique + 11) % 12)
			resultat = numeroNote + 9; /* Correspond à la 6te majeure supérieure. Exemple : B3->G#3 */

		/* Prévention contre résultats négatifs */
		if (resultat < 0) {
			resultat = resultat + 12;
		}

		int octaveResultat = resultat / 12;

		if (octaveResultat < octaveInitial) {
			resultat = resultat + 12;
		} else if (octaveResultat > octaveInitial) {
			resultat = resultat - 12;
		}

		return resultat;
	}

	/**
	 * Principe : on échange les notes entre elles par rapport à l'axe de symétrie
	 * constitué par IIb+II de l'octave 5. Donc les octaves font : 0<->10, 1<->9,
	 * 2<->8, 3<->7, 4<->6, 5<->5.
	 */
	public static int negationCentrale(int numeroNote, String tonique) {
		int octaveInitiale = numeroNote / 12;

		int numeroNoteDansOctave = numeroNote % 12;

		int numeroTonique = mapNoteNumero.get(tonique);

		/*
		 * L'idée est d'identifier la position de la note par rapport à la tonique, et
		 * de la transposer en fonction de cela.
		 */

		int resultat = numeroNote;

		if (numeroNoteDansOctave == numeroTonique % 12)
			resultat = numeroNote + 7; /* Correspond à la 5te juste supérieure. Exemple : C3->G3 */
		else if (numeroNoteDansOctave == (numeroTonique + 1) % 12)
			resultat = numeroNote + 5; /* Correspond à la 4te juste supérieure. Exemple : C#3->F#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 2) % 12)
			resultat = numeroNote + 3; /* Correspond à la 3rce mineure supérieure. Exemple : D3->F3 */
		else if (numeroNoteDansOctave == (numeroTonique + 3) % 12)
			resultat = numeroNote + 1; /* Correspond à la 2nde mineure supérieure. Exemple : D#3->E3 */
		else if (numeroNoteDansOctave == (numeroTonique + 4) % 12)
			resultat = numeroNote - 1; /* Correspond à la 2nde mineure inférieure. Exemple : E3->D#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 5) % 12)
			resultat = numeroNote - 3; /* Correspond à la 3rce mineure inférieure. Exemple : F3->D3 */
		else if (numeroNoteDansOctave == (numeroTonique + 6) % 12)
			resultat = numeroNote - 5; /* Correspond à la 4te juste inférieure. Exemple : F#3->C#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 7) % 12)
			resultat = numeroNote - 7; /* Correspond à la 5te juste inférieure. Exemple : G3->C3 */
		else if (numeroNoteDansOctave == (numeroTonique + 8) % 12)
			resultat = numeroNote - 9; /* Correspond à la 6te majeure inférieure. Exemple : G#3->B3 */
		else if (numeroNoteDansOctave == (numeroTonique + 9) % 12)
			resultat = numeroNote - 11; /* Correspond à la 7eme majeure inférieure. Exemple : A3->A#3 */
		else if (numeroNoteDansOctave == (numeroTonique + 10) % 12)
			resultat = numeroNote + 11; /* Correspond à la 7eme majeure supérieure. Exemple : A#3->A3 */
		else if (numeroNoteDansOctave == (numeroTonique + 11) % 12)
			resultat = numeroNote + 9; /* Correspond à la 6te majeure supérieure. Exemple : B3->G#3 */

		/* Prévention contre résultats négatifs */
		if (resultat < 0) {
			resultat = resultat + 12;
		}

		int octaveResultat = 10 - octaveInitiale;

		resultat = getNumFromNomNote(getNomNoteSansOctaveFromNum(resultat) + octaveResultat);

		return resultat;
	}

	/* Méthodes utilitaires */

	/**
	 * Récupère le nom d'une note, avec son placement dans les octaves, à partir de
	 * son n° midi. Exemple : 0->"C0", 68->"G#5".
	 */
	public static String getNomNoteAvecOctaveFromNum(int numeroNote) {
		int octave = (numeroNote / 12);
		return getNomNoteSansOctaveFromNum(numeroNote) + octave;
	}

	/**
	 * Récupère le nom d'une note, sans son placement dans les octaves, à partir de
	 * son n° midi. Exemple : 0->"C", 68->"G#".
	 */
	public static String getNomNoteSansOctaveFromNum(int numeroNote) {
		int note = numeroNote % 12;
		return (String) getKeyFromValue(mapNoteNumero, note);
	}

	/**
	 * Récupère le n° midi d'une note (avec son placement dans les octaves) à partir
	 * de son nom. Exemple : "C0"->0, "G#5"->68.
	 */
	public static int getNumFromNomNote(String nomNote) {
		String nomNoteSansOctave = nomNote.replaceAll("[0-9]", "");
		int octave = Integer.parseInt(nomNote.replaceAll("[^0-9]", ""));

		return octave * 12 + mapNoteNumero.get(nomNoteSansOctave);
	}

	/**
	 * Méthode utilitaire pour trier une Map du plus grand au plus petit.
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	/**
	 * Méthode utilitaire pour récupérer la clé d'une map en fonction de sa valeur
	 * (donc à n'utiliser que dans le cas d'une map bijective si on ne veut pas de
	 * multiples résultats possibles pour une même valeur)
	 */
	public static Object getKeyFromValue(Map<?, ?> hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}

}
