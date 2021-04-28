
package assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Burcu Boran
 */
@SuppressWarnings("unused")
public class Assistant {

	/**
	 * This is a program for reading a text file and parsing it accordigly and
	 * creating an output Json file. For a given employee id, we will be able to get
	 * start and end of the meeting in date and time format, the duration of the
	 * meeting, earliest and latest meeting for each employees. For future it can be
	 * developed further by coding RestAPI calls/requests. At the moment it does not
	 * generate a Json output, therefore no API calls can be made.
	 * 
	 * @param args
	 */
	private String id = "";

	/**
	 * This is the class scope and we declared an Id in String format in order to
	 * use it in HashMap later. One other declaration is the ArrayList of the
	 * Meetings. By creating this structure, we can store the requested data in this
	 * list.
	 */

	private ArrayList<Meeting> meetings = new ArrayList<>();

	public static void main(String[] args) throws JSONException {

		/**
		 * This is the main class. In its scope, we declared an Id in String format in
		 * order to use it to compare in HashMap later. We also declared an array of
		 * strings called line for each employees meeting data.
		 */
		String id = "";
		String[] line;

		/**
		 * A HashMap declaration called employees, for storing meeting arraylist and ids
		 * of the employees.
		 */
		HashMap<String, Assistant> employees = new HashMap<String, Assistant>();

		try {
			
			/**
			 * Reading the text file provided, using Scanner object.
			 */
			Scanner scanner = new Scanner(new File("freebusy.txt"));
			while (scanner.hasNextLine()) {
				
				/**
				 * the meeting line (arraylist of meetings` line) has been splitted in order to
				 * get the id of the employee,the start time of the meeting and the end time of
				 * the meeting.
				 */
				line = scanner.nextLine().split(";");
				/**
				 * each meeting line consists of 4 rows of data therefore we disclude the
				 * null(empty)oned and also the last line has only 1 row and the first row has 5
				 * and such. Note from the client is ;due to the crappy state of the existing
				 * system the file may contain some irregularities, these should be ignored!
				 */
				if (line.length == 4) {
					id = line[0];
					
					/**
					 * Creating a controller to check whether the employee id exist or not in the
					 * HashMap. And if the employee exist in the list, list the each meetings` start
					 * and end time.
					 */
					Assistant tmpAssistant = employees.get(id);
					if (tmpAssistant != null) {
						tmpAssistant.addTime(line[1], line[2]);
					}
					
					/**
					 * Else condition defined in Line80, if the employee does not exist in the list,
					 * add and list the each meetings` start and end time.
					 */
					else {
						employees.put(id, new Assistant(id));
						employees.get(id).addTime(line[1], line[2]);
					}
				}

				/**
				 * Else condition for checking the how many rows in a line.
				 * If the line has 2 rows instead of 4, then it has the name column which we do
				 * not want to include in the output.
				 */
				else if (line.length != 2) {
					// System.out.println(Arrays.toString(line));
				}
			}

//				Assistant employee = employees.get("177736372484123384037491644729334788901");
//				employee.sortMeetingDate();
//				System.out.println(employee);

			scanner.close();

			/**
			 * The try catch exception to check whether the file is found or not. IO File
			 * Not Found Exception is thrown.
			 */
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		/**
		 * Creating a Json Array of Object called employeelist in order to eventually
		 * create a Json file format output.
		 */
		JSONArray employeeList = new JSONArray();
		
		/**
		 * Traversing in a newly created Map Entry in order to store the details of the
		 * each meetings.
		 */
		for (Map.Entry<String, Assistant> entry : employees.entrySet()) {

			Assistant employee = entry.getValue();
			JSONObject employeeDetails = new JSONObject();
			employeeDetails.put("ID", employee.getId());
			employeeDetails.put("Office Hours", "8-17");
			employeeDetails.put("Earliest Meeting", employee.earliestMeeting());
			employeeDetails.put("Latest Meeting", employee.latestMeeting());
			
			/**
			 * The nested loop is needed as we are expected to use getter methods for start
			 * and end and duration of the each meetings.
			 */
			for (int i = 0; i < employee.getNumberOfMeetings(); i++) {
				employeeDetails.put("Start Time", employee.getMeeting(i).getStart());
				employeeDetails.put("End Time", employee.getMeeting(i).getEnd());
				employeeDetails.put("Duration", employee.getMeeting(i).calcDuration());
			}
			
			/**
			 * We created another Json object called employeeData. To this list we added the
			 * employee ids to employeeData and added the previous employeeDetails list data
			 * to the matching employee id. And finally we added all these data to the first
			 * list we created, employeeList.
			 */
			JSONObject employeeData = new JSONObject();
			employeeData.put(employee.id, employeeDetails);
			employeeList.put(employeeData);
		}
		
		/**
		 * Creating and writing the Json file using FileWriter Json class. Employeelist
		 * is out main Json Object list is written in Json format.
		 */
		try (FileWriter file = new FileWriter("employees.json")) {
			file.write(employeeList.toString());
			file.flush();

		} 
		/**
			 * The try catch exception to check whether the Json file is found or not. IO
			 * File Not Found Exception is thrown.
			 */
		catch (IOException e) {
			System.err.println("IO EXception: employees.json");
			e.printStackTrace();
		}

	}

	/**
	 * Main method ends here.
	 */

	/**
	 * This class is a constructor called Assistant takes a parameter for each
	 * employee id. It also has its getter methods. And also, calculated the
	 * duration time of the meeting.
	 */
	public Assistant(String id) {
		this.id = id;
	}

	/**
	 * Getter methods
	 */
	/**
	 * Getter for the employee ids.
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter for the amount of the meetings.
	 * @return meetings.size
	 */
	public int getNumberOfMeetings() {
		return meetings.size();
	}

	/**
	 * Getter for the meetings. It is a Meeting object where Meeting is an inner class.
	 * @return meeting(s)
	 */
	public Meeting getMeeting(int i) {
		return meetings.get(i);
	}

	/**
	 * Getter for the date of the meeting(s).
	 * @return date
	 */
	public String getDate(int i) {
		return meetings.get(i).getDate();
	}

	/**
	 * For each meeting, we add the start and add time, using this method.
	 */
	public void addTime(String start, String end) {
		meetings.add(new Meeting(start, end));
	}

	/**
	 * Getter for the duration time of the meeting(s).
	 * @return duration(s)
	 */
	public int getCalcDuration(int i) {
		return meetings.get(i).calcDuration();
	}

	/**
	 * Lambda function to sort meetings from shortest time to longest time order.
	 * This method uses compareTo logic. The end of the meeting time is substracted
	 * from the start/beginning of the meeting time in order to find its duration.
	 * 
	 * @return sorted duration
	 */
	public void sortMeetingLength() { 
		meetings.sort((o1, o2) -> o1.calcDuration() - o2.calcDuration());

	}
	
	/**
	 * This method is to parse the meeting text data and eventually to retrieve the month and the day of the meeting(s).
	 * The if clause checks if the month and date are the same in comparision then sort by the day data, otherwise compate by month data,
	 * @return result of sorted date
	 */
	public void sortMeetingDate() {
		meetings.sort((o1, o2) -> {
			String[] dateDelimeter1 = o1.getDate().split("/");// parsing the werstring formatted date.
			String[] dateDelimeter2 = o2.getDate().split("/");// parsing the werstring formatted month.
			int result = Integer.parseInt(dateDelimeter1[0]) - Integer.parseInt(dateDelimeter2[0]); // month
			if (result == 0) {
				result = Integer.parseInt(dateDelimeter1[1]) - Integer.parseInt((dateDelimeter2[1])); // day
			}
			return result;
		});

	}

	/**
	 * Getter for the longest meeting. Not requested.
	 * @return longest  meeting
	 */
	public Meeting longestMeeting() {
		sortMeetingLength();
		return meetings.get(meetings.size() - 1); // as it is already sorted last element is the longest
	}

	/**
	 * Getter for the shortest meeting. Not requested.
	 * @return shortest meeting
	 */
	public Meeting shortestMeeting() {
		sortMeetingLength();
		return meetings.get(0);// as it is already sorted first element is the shortest
	}
	
	/**
	 * Getter for the earliest meeting.
	 * @return earliest meeting
	 */
	public Meeting earliestMeeting() {
		sortMeetingDate();
		return meetings.get(0);
	}
	
	/**
	 * Getter for the latest meeting.
	 * @return latest meeting 
	 */
	public Meeting latestMeeting() {
		sortMeetingDate();
		return meetings.get(meetings.size() - 1);
	}
	
	/**
	 * ToString method,creating a new String Builder obj and appending for each id, their corresponding meeting details, duration(s).
	 * @return  meeting id,duration(s)
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(" + id + ") : \n");
		for (Meeting tmp : meetings) {
			sb.append(tmp + "   " + tmp.calcDuration() + "\n");
		}
		return sb.toString();
	}
	
	/**
	 * This class is an inner class called Meeting.
	 * It scope contains start and end time of the meeting(s). An array is constructed for parsing purposes.
	 */
	// InnerClass
	// TODO Auto-generated method stub
	private class Meeting {
		String start;
		String end;
		String[] wholepart1;

		Meeting(String start, String end) {
			this.start = start;
			this.end = end;
			wholepart1 = start.split(" ");
		}
		
		/**
		 * Getter for the finish time of the meeting.
		 * @return ending time 
		 */
		public String getEnd() {

			return end;
		}
		
		/**
		 * Getter for the start time of the meeting.
		 * @return start time 
		 */
		public String getStart() {
			return start;
		}

		/**
		 * This method is to calculate the duration time of the meeting(s), and called calcDuration. 
		 * Earlier we could get/pull  each meeting from the meeting list and their durations. 
		 * By this method, we calculate the duration of each meeting in minutes.
		 * To be able to that, we use autoboxin and unboxing and casting the integer value minutes to string and also compare the durations. 
		 * Each comparation is made with start and end time of the meeting in minutes.
		 * The note from the client applies here too and a line which does not contain "AM || PM" string is found/detected but dismissed in this method.
		 * @return start time 
		 */
		public int calcDuration() { 
			int duration1;
			String[] wholepart2 = end.split(" ");
			String[] start1 = wholepart1[1].split(":");
			String[] end1 = wholepart2[1].split(":");

			if (!(wholepart1.length <= 2 || wholepart2.length <= 2)) { //checking the number of rows in the date line.

				if (!wholepart1[2].equals(wholepart2[2])) {
					duration1 = Math.abs(((Integer.parseInt(end1[0]) + 12) * 60 + Integer.parseInt(end1[1]))
							- (Integer.parseInt(start1[0]) * 60 + Integer.parseInt(start1[1])));
				} else {
					duration1 = Math.abs(((Integer.parseInt(end1[0])) * 60 + Integer.parseInt(end1[1]))
							- (Integer.parseInt(start1[0]) * 60 + Integer.parseInt(start1[1])));
				}
			} else {
				return -1;
			}

			return duration1;
		}

		/**
		 * Getter for the complete whole date data without the clock time.
		 * @return date
		 */
		public String getDate() {
			return wholepart1[0];
		}
		
		/**
		 * ToString method,directly returning start and end time of the meeting(s).
		 * @return start,end time
		 */
		public String toString() {
			return start + " " + end;
		}
	}
}
