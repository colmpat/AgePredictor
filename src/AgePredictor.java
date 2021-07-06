import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class AgePredictor {

    private Container container;
    private String directory;
    private LinkedList<Name> ll;
    private ArrayList<Name> arr;
    private enum Container {
        ARRAYLIST,
        LINKEDLIST
    }

    public AgePredictor(Container container, String directory) {
        this.container = container;
        this.directory = directory;
        if(container == Container.ARRAYLIST)
            this.arr = new ArrayList<Name>();
        else 
            this.ll = new LinkedList<Name>();
        try {
            fetch();
        } catch (Exception e) {
            System.out.println("There was an error with the provided directory. Please ensure all files therein are valid.");
            System.out.println("Error: " + e.getLocalizedMessage());
            System.exit(-1);
        }
    }

    //returns a string giving the age(range) prediction
    public String getPrediction(Name name) throws Exception {
        Exception noMatch = new Exception("The given information found no matches, no prediction could be made.");

        int[] birthYearRange = new int[2]; //this array is populated acting as a range, iff there are multiple valid predictions
        
        //performs container-specific result generation
        if (this.container == Container.ARRAYLIST) {
            try {
                arrayListPrediction(name, birthYearRange);
            } catch (Exception e) {
                throw noMatch;
            }
        } 
            
        else 
            linkedListPrediction(name, birthYearRange);

        //post iteration checks

        //if we found no matches
        if(name.count == 0)
            throw noMatch;

        //convert birth years to ages

        String age = "" + (2021 - name.birthYear);

        //if there is a range of possible ages
        if(birthYearRange[0] != 0) {
            birthYearRange[0] = 2021 - birthYearRange[0];
            birthYearRange[1] = 2021 - birthYearRange[1];
            age = birthYearRange[0] + " to " + birthYearRange[1];
        }

        name.birthYear = 2021 - name.birthYear;
        

        return name.name + ", born in " + name.state + " is most likely around " + age +  " years old." ;
    }

    /**
     * iterates through data in arr and updates name.count and name.birthYear 
     * when a new maximum of count is found for the person's given information
     * when there are multiple maximum counts, a range of birthYears is updated
     */
    
    private void arrayListPrediction(Name name, int[] birthYearRange) throws Exception {
       
        for(int i = 0; i < arr.size; i++) {
            Name curr = arr.get(i);

            //if the given fields don't match the current name, iterate
            if(curr.sex != name.sex || !curr.state.equals(name.state) || !curr.name.equals(name.name))
                continue;
            
            //check for a new highest count
            if(curr.count > name.count) {
                name.count = curr.count;
                name.birthYear = curr.birthYear; //store the most likely birthYear and its corresponding count then iterate
                birthYearRange = new int[] {0,0}; //reset range because there is now only one prediciton
                continue;
            }

            //check for duplicate count, which would mean there are multiple age predictions of equal probability
            if(curr.count == name.count) {
                //if greater than first (new youngest range) 2010 > 1995 therefore 2010 is new youngest range
                if(curr.birthYear > birthYearRange[0])
                    birthYearRange[0] = curr.birthYear;
                //else if less than second (new oldest range) 1960 < 1985 therefore 1960 is new oldest range
                else if(curr.birthYear < birthYearRange[1])
                    birthYearRange[1] = curr.birthYear; 
            }
        }
    }

    private void linkedListPrediction(Name name, int[] birthYearRange) {
        Node<Name> curr = ll.head;

        while(curr != null) {
            //if the given fields don't match the current name, iterate
            if(curr.data.sex != name.sex || !curr.data.state.equals(name.state) || !curr.data.name.equals(name.name)) {
                curr = curr.next;
                continue;
            }
            
            //check for a new highest count
            else if(curr.data.count > name.count) {
                name.count = curr.data.count;
                name.birthYear = curr.data.birthYear; //store the most likely birthYear and its corresponding count then iterate
                curr = curr.next;
                birthYearRange = new int[] {0,0}; //reset range because there is now only one prediciton
                continue;
            }

            //check for duplicate count, which would mean there are multiple age predictions of equal probability
            else if(curr.data.count == name.count) {
                //if greater than first (new youngest range) 2010 > 1995 therefore 2010 is new youngest range
                if(curr.data.birthYear > birthYearRange[0])
                    birthYearRange[0] = curr.data.birthYear;
                //else if less than second (new oldest range) 1960 < 1985 therefore 1960 is new oldest range
                else if(curr.data.birthYear < birthYearRange[1])
                    birthYearRange[1] = curr.data.birthYear;
                curr = curr.next; 
            }

            //if all the fields match but there is not a new max for count we iterate
            else
                curr = curr.next;
        }

    }

    //fetches data from the provided directory and stores it into the instance's corresponding container 
    private void fetch() throws Exception {
        System.out.println("Loading the data you provided...");
        long start = System.currentTimeMillis();
        File folder = new File(this.directory);
        for(File file : folder.listFiles()) {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] toks = line.split(","); //tokenize line on the comma
                if(toks.length < 5) {
                    sc.close();
                    throw new Exception("Data error. The file is poorly formatted");
                }
                Name name = 
                    new Name(
                            toks[0], 
                            toks[1].charAt(0), 
                            Integer.parseInt(toks[2]), 
                            toks[3], 
                            Integer.parseInt(toks[4])
                        );

                if(this.container == Container.ARRAYLIST)
                    arr.add(name);
                else
                    ll.add(name);
            }

            sc.close();
        }
        System.out.println("Data loaded in " + (System.currentTimeMillis() - start) + "MS.");
    }

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Error, usage: java AgePredictor inputFile");
            return;
        }

        //Create new initial properties
        Properties properties = new Properties();
        //Open reader to read given file
        try (FileReader in = new FileReader(args[0])) {
            //load properties from file
            properties.load(in);
        } catch (IOException e) {
            System.out.println("There was an error with the provided file. Ensure the file is valid.");
            System.out.println("Error: " + e.getLocalizedMessage());
        }

        //We first must parse the properties file to ensure the state variables of AgePredictor will be valid before we can proceed.
        
        //setting container value from properties and ensuring its validity
        Container container;
        switch(properties.getProperty("ListType")) {
            case "ArrayList":
                container = Container.ARRAYLIST;
                break;
            case "LinkedList":
                container = Container.LINKEDLIST;
                break;
            default:
                System.out.println("The properties file you provided does not have a valid ListType=value set.\nPlease update the properties file before continuing. Valid values are \"LinkedList\" and \"ArrayList\"");
                return;
        }
        //setting directory value from properties and ensuring its validity
        final String directory = properties.getProperty("Directory");
        final File file = new File(directory); //this is to ensure the path we are providing has at least one file in the directory
        if(directory == null || file.listFiles() == null) {
            System.out.println("The properties file you provided does not have a valid Directory=value set. Please update the properties file before continuing.");
            return;
        }

        //now we prompt the user for information
        AgePredictor predictor = new AgePredictor(container, directory);
        Scanner sc = new Scanner(System.in);
        do {
            //get name
            String name = "";
            System.out.print("Name of the person (or EXIT to quit): ");
            name = sc.next(); //we use next becuase we just want the first word
            sc.nextLine(); //to catch the '\n' the user enters
            if(name.equals("EXIT")) //our only exit case from the loop
                return;
            
            //get sex
            String sex = "";
            do {
                System.out.print("Sex (M/F): ");
                sex = sc.next();
                sc.nextLine();
                if(!sex.equals("F") && !sex.equals("M")) { //if the inputted sex is not F or M
                    sex = ""; //force while loop to re-run
                    System.out.println("Please enter a valid sex (M/F)");
                }
            } while(sex.length() == 0);
            
            //get state
            String state = "";
            final String[] states = {"AL","AK","AZ","AR","CA","CO","CT","DE","DC","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA","VI","WA","WV","WI","WY"};
            do {
                System.out.print("State of birth (two-letter state code): ");
                state = sc.next();
                boolean validState = false;
                for(String st : states) {
                    if(st.equals(state)) {
                        validState = true;
                        break;
                    }
                }
                if(!validState) {
                    System.out.println("Please enter a valid state code. All valid state codes: ");
                    for(String st : states)
                        System.out.print(st + " ");
                    System.out.println();
                    state = "";
                }

            } while(state.length() == 0);

            //now generate prediction
            Name person = new Name(state, sex.charAt(0), 0, name, 0);
            try {
                System.out.println(predictor.getPrediction(person));
            } catch (Exception e) {
                System.out.println("Error: " + e.getLocalizedMessage());
            }
        } while(true);
        
    }
}