package info.ferrarimarco.uniroma2.is.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private CategoryPersistenceService categoryPersistenceService;

    @Autowired
    private ClazzPersistenceService clazzPersistenceService;

    @Autowired
    private ProductPersistenceService productPersistenceService;

    @Override
    public Double deperibilita() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double successo(String entityId, Class<? extends Entity> clazz) {
        Long totalDispensed = 0L;
        Long totalRequested = 0L;
        int pageIndex = 0;
        
        if(Category.class.equals(clazz)){
            Category category = categoryPersistenceService.findById(entityId);
            Page<Product> products = productPersistenceService.findByCategory(category, new PageRequest(pageIndex, 10));
            
            
            
        }else if(Clazz.class.equals(clazz)){
            e = clazzPersistenceService.findById(entityId);
        }else if(Product.class.equals(clazz)){
            e = productPersistenceService.findById(entityId);
        }else{
            throw new IllegalArgumentException("Entity class not handled");
        }
        
        //VERIFICO SE VOGLIO CALCOLARE L'INDICE DI UNA CLASSE ALIMENTARE o DI UN PRODOTTO
        if(CostantiClassiAlimentari.containsClasseAlimentare(k.toString()))
        {
            if(CostantiClassiAlimentari.containsClasseBase(k.toString()))
            {
                Double erogazioneTotaleClasse = new Double(0);
                Double richiesteTotaliClasse =  new Double(0);
                Set <String> tutteLePortate= keySet();
                for (String s : tutteLePortate)     //Individuo oggetti di Tipo ClasseBase
                {
                    Portata oggettoPortata = getMappaProdotti().get(s);
                    if((oggettoPortata.getClass().getName()).equals(k.toString()))  //Se Ã¨ un oggetto di Classe k
                    {
                        erogazioneTotaleClasse = erogazioneTotaleClasse + oggettoPortata.getErogate();
                        richiesteTotaliClasse = richiesteTotaliClasse + oggettoPortata.getRichieste();
                    }
                }
                if(richiesteTotaliClasse>0)
                {
                    Double successo = new Double(erogazioneTotaleClasse/richiesteTotaliClasse);
                    return successo;
                }
                else throw new ArithmeticException ("Al momento nessun prodotto di questo tipo risulta ancora erogato/a. Impossibile calcolarne l'indice di Successo!");   
            }
            if(CostantiClassiAlimentari.containsCategoria(k.toString()))
            {
                Double erogazioneTotaleCategorie = new Double(0);
                Double richiesteTotaliCategorie=  new Double(0);
                Set <String> tutteLePortate= keySet();
                int n=0;
                for (String s : tutteLePortate)     //Individuo oggetti di Tipo Categoria
                {
                    Portata oggettoPortata = getMappaProdotti().get(s);
                    if(CalcolaSuperClasseDi(oggettoPortata).equals(k.toString()))
                    {
                        erogazioneTotaleCategorie = erogazioneTotaleCategorie + oggettoPortata.getErogate();
                        richiesteTotaliCategorie = richiesteTotaliCategorie + oggettoPortata.getRichieste();
                        n++;
                    }                   
                }
                System.out.println("ErogazioneTotaleCategorie: "+erogazioneTotaleCategorie);
                System.out.println("RichiesteTotaliCategorie: "+richiesteTotaliCategorie);
                System.out.println("Trovati "+n+" prodotti di classe"+k.toString());
                if(richiesteTotaliCategorie>0)
                {
                    Double successo = new Double(erogazioneTotaleCategorie/richiesteTotaliCategorie);
                    //return mappaProdotti.get(k.toString()).gradimentoMedioGiornaliero(gradimentoMedioGiornaliero);
                    return successo;
                }
                else throw new ArithmeticException ("Al momento nessun prodotto di questo tipo risulta ancora erogato/a. Impossibile calcolarne l'indice di Successo!");    
            }
            else    //Indice su classe Madre: Calcolo tutti gli oggetti!
            {
                System.out.println("Metodo A");
                if(!k.toString().equals("Portata")) 
                {
                    System.out.println("Attenzione Indice Totale non di Portata!?!?!");
                    return -1;
                }

                Double erogazioneComplessiva = new Double(0);
                Double richiesteComplessive=  new Double(0);
                Set <String> tutteLePortate= keySet();
                int n=0;
                for (String s : tutteLePortate)
                {
                    Portata oggettoPortata = getMappaProdotti().get(s);

                    erogazioneComplessiva = erogazioneComplessiva + oggettoPortata.getErogate();
                    richiesteComplessive = richiesteComplessive + oggettoPortata.getRichieste();
                    n++;            
                }
                System.out.println("Trovati "+n+" prodotti di classe "+k.toString());       
                if(richiesteComplessive>0)
                {
                    Double successo = new Double(erogazioneComplessiva/richiesteComplessive);
                    //return mappaProdotti.get(k.toString()).gradimentoMedioGiornaliero(gradimentoMedioGiornaliero);
                    return successo;
                }
                else throw new ArithmeticException ("Al momento nessun prodotto di questo tipo risulta ancora erogato/a. Impossibile calcolarne l'indice di Successo!"); 
            }
        }
        else  //Ã¨ un Prodotto
        {
            if (mappaProdotti.get(k.toString())==null)
            {
                throw new ProdottoException("\nControllore Amministrazione successo(). Prodotti " + k.toString() + "  non sono presenti\n");
            }

            Portata p = mappaProdotti.get(k.toString()); 
            double a = p.successo();
            return a;   
        }

    }

    @Override
    public Double gradimento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double gradimentoMedioGiornaliero() {
        // TODO Auto-generated method stub
        return null;
    }

}
