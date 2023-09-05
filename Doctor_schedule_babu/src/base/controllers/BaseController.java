package base.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import base.daos.DoctorDetailsDAO;
import base.daos.UserDAO;
import base.models.AppointmentSchedule;
import base.models.Doctor;
import base.models.User;
import base.models.UserCred;

@Controller
public class BaseController {
	List<AppointmentSchedule> s;
	boolean b;
	String slotid;

	UserDAO userdal;
	DoctorDetailsDAO doctorddal;

	@Autowired
	public BaseController(UserDAO u, DoctorDetailsDAO dd) {
		userdal = u;
		doctorddal = dd;
	}

	@RequestMapping(value = "/verifyLogin")
	public String home(UserCred userCred, Model model) {
		String password = userCred.getPassword();
		User user = userdal.getUserById(userCred.getUsername());
		if (password.equals(user.getuPass())) {
			model.addAttribute("doctorsData", doctorddal.getAllDoctorsInfo());
			model.addAttribute("specializationData", doctorddal.getAllSpecializations());
			return "DoctorList";
		} else {
			return "wrongCred";
		}
	}

	@RequestMapping(value = "/doctorList", method = RequestMethod.GET)
	public String doctorList(@RequestParam("specialization") String specialization, Model model) {
		model.addAttribute("specializationData", doctorddal.getAllSpecializations());
		model.addAttribute("doctorsData", doctorddal.getAllDoctorsBySpecialization(specialization));
		return "DoctorList";
	}

	@RequestMapping(value = "/doctorDetails", method = RequestMethod.GET)
	public String doctorDetails(@RequestParam("doctorid") int doctorid, Model model) {
		model.addAttribute("doctor", doctorddal.getDoctorInfoById(doctorid));
		return "DoctorDetails";
	}

	@RequestMapping(value = "/doctorCalendar", method = RequestMethod.GET)
	public String doctorCalender(@RequestParam("doctorid") int doctorid, Model model) {
		model.addAttribute("doctorSchedule", doctorddal.getAppointmentScheduleById(doctorid));
		System.out.println(doctorddal.getAppointmentScheduleById(doctorid).get(0).getSlotfrom());
		System.out.println(doctorid);
		return "DoctorCalendar";
	}

	@RequestMapping(value = "/getDoctorSchedule", method = RequestMethod.GET)
	@ResponseBody
	public List<AppointmentSchedule> doctorSchedule(@RequestParam("doctorid") int doctorid, Model model) {
		return doctorddal.getAppointmentScheduleById(doctorid);
	}

	@RequestMapping(value = "/SlotsUpdated", method = RequestMethod.POST)
	public String handleBooking(@RequestParam("doctorId") String doctorId,
			@RequestParam("selectedDate") String selectedSlot)
	// @RequestParam("selectedSlotId") String selectedSlotId)
	{
		slotid = selectedSlot;

		System.out.println("Doctor ID: " + doctorId);
		System.out.println("Selected slot id : " + selectedSlot);
		s = doctorddal.timeSlots(Integer.parseInt(selectedSlot));

		// System.out.println("Selected Slot ID: " + selectedSlotId);

		// Redirect to a confirmation page or any other appropriate page.
		return "patientDetails"; // This should match the name of your HTML template.
	}

	@RequestMapping(value = "/BookingDetails", method = RequestMethod.POST)
	public String Booking(@RequestParam("doctorId") String doctorId, @RequestParam("selectedSlot") String selectedSlot,
			@RequestParam("patientId") String patientId, @RequestParam("name") String name,
			@RequestParam("gender") String gender, @RequestParam("phone") String phone, @RequestParam("age") int age,
			Model model) {

		// Now you have access to all patient details
		b = doctorddal.updateStatus(slotid);
		System.out.println(b);
		System.out.println("Doctor ID: " + doctorId);
		System.out.println("Selected Date booking: " + selectedSlot);
		System.out.println("Patient ID: " + patientId);
		System.out.println("Name: " + name);
		System.out.println("Gender: " + gender);
		System.out.println("Phone: " + phone);
		System.out.println("Age: " + age);

		// Get doctor details and add them to the model
		List<Doctor> doctorDetails = doctorddal.doctorDetails(Integer.parseInt(doctorId));

		model.addAttribute("doctor", doctorDetails);

		System.out.println(s.get(0).getSlotfrom());
		// Add patient details to the model
		model.addAttribute("patientId", patientId);
		model.addAttribute("selectedSlot", selectedSlot);
		model.addAttribute("name", name);
		model.addAttribute("gender", gender);
		model.addAttribute("phone", phone);
		model.addAttribute("age", age);
		model.addAttribute("SlotTimeFrom", s.get(0).getSlotfrom());
		model.addAttribute("SlotTimeTo", s.get(0).getSlotto());
		model.addAttribute("SlotTimeDate", s.get(0).getSlotdate());

		// Redirect to a confirmation page or any other appropriate page.
		return "Booking"; // This should match the name of your HTML template.
	}

}