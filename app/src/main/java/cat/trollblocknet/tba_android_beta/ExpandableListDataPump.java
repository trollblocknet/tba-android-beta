package cat.trollblocknet.tba_android_beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> trollblocknet = new ArrayList<String>();
        trollblocknet.add("Trolls \"unionistes\\Constitucionalistes\".");
        trollblocknet.add("Infiltrats que semblen indepes que inciten a la violència.");
        trollblocknet.add("Masclistes, xenòfobs, homòfobs, catalanòfobs.");
        trollblocknet.add("Feixises, franquistes i nazis en general.");

        List<String> xusmablocknet = new ArrayList<String>();
        xusmablocknet.add("Membres de la Casa Reial");
        xusmablocknet.add("Representants del Govern Espanyol");
        xusmablocknet.add("Forces armades i cossos de seguretat");
        xusmablocknet.add("Partits polítics unionistes i els seus membres");
        xusmablocknet.add("\"Fatxes amb càrrec\" (Jutges, advocats, periodistes, etc");

        List<String> ibexblocknet = new ArrayList<String>();
        ibexblocknet.add("Empreses IBEX-35");
        ibexblocknet.add("Col·laboradors financers Règim del 78");

        expandableListDetail.put("1. Troll o Infiltrat", trollblocknet);
        expandableListDetail.put("2. Persona \"non-gratae\"", xusmablocknet);
        expandableListDetail.put("3. Empresa IBEX-35 o Col·laborador Règim 78", ibexblocknet);
        return expandableListDetail;
    }
}