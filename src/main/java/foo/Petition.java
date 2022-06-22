package foo;

import java.util.HashSet;

public class Petition {
    public String ID;
	public String titre;
	public String theme;
    public String description;
    public int nombreSignature;
    public String proprietaire;
	public String nomProprietaire;
    HashSet<String> signataires;
	public Petition() {}
}
