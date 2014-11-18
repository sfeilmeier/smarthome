/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import java.util.ArrayList;

public class CessProtocolFactory {
	public static EssProtocol getProtocol(String modbusinterface, int unitid) {
		// TODO: Allow Reserved-Words
		//TODO: New type ItemPercentage
		ArrayList<ModbusElementRange> wordRanges = new ArrayList<ModbusElementRange>();
		
		// PCS
		wordRanges.add(	new ModbusElementRange(0x0100,
			new BitWordElement("Unit_state",
				new OnOffBitItem(0, "System_warning_state"),
				new OnOffBitItem(1, "System_fault_state"),
				new OnOffBitItem(2, "System_power_decrease_state"),
				new OnOffBitItem(3, "System_complete_charge_state"),
				new OnOffBitItem(4, "System_complete_discharge_state") ),
			new BitWordElement("Unit_work_state",
				new OnOffBitItem(0, "Initial"),
				new OnOffBitItem(1, "Fault"),
				new OnOffBitItem(2 ,"Stop"),
				new OnOffBitItem(3 ,"Standby"),
				new OnOffBitItem(4 ,"Grid_Monitoring"),
				new OnOffBitItem(5 ,"Ready"),
				new OnOffBitItem(6 ,"Running"),
				new OnOffBitItem(7 ,"Debug") ),
			new BitWordElement("Unit_ctrl_mode",
				new OnOffBitItem(0, "Remote"),
				new OnOffBitItem(1, "Fault"),
				new OnOffBitItem(2 ,"Stop") ),
			new BitWordElement("Unit_work_mode",
				new OnOffBitItem(5, "Island") ) ) );
		wordRanges.add(	new ModbusElementRange(0x0110, 
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(2, "Emergency_stop") ),
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(0, "Environment_temperature_sensor_invalidation"),
				new OnOffBitItem(3, "Transformer_phase_B_temperature_sensor_invalidation"),
				new OnOffBitItem(9, "Environment_humidity_sensor_invalidation"),
				new OnOffBitItem(12, "SD_memory_card_invalidation"),
				new OnOffBitItem(13, "Environment_humidity_over_limit") ) ) );
		wordRanges.add(	new ModbusElementRange(0x0125, 
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(0, "Inverter_communication_abnormity"),
				new OnOffBitItem(1, "Battery_stack_communication_abnormity"),
				new OnOffBitItem(4, "Remote_communication_abnormity"),
				new OnOffBitItem(6, "DC_converter_communication_abnormity") ),
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(0, "Environment_severe_overtemperature"),
				new OnOffBitItem(3, "Transformer_severe_overtemperature") ) ) );
		wordRanges.add( new ModbusElementRange(0x0150, 
			new BitWordElement("Switch_state",
				new OnOffBitItem(0, "DC_breaker_state"),
				new OnOffBitItem(1, "DC_main_contactor_state"),
				new OnOffBitItem(2, "DC_precharge_contactor_state"),
				new OnOffBitItem(3, "AC_breaker_state"),
				new OnOffBitItem(4, "AC_main_contactor_state"),
				new OnOffBitItem(5, "AC_precharge_contactor_state"),
				new OnOffBitItem(6, "Emergency_stop_button_state"),
				new OnOffBitItem(7, "Control_switch_state") ),
			new BitWordElement("Switch_state",
				new OnOffBitItem(0, "Overcurrent_self_lock_state"),
				new OnOffBitItem(1, "Remote_start_signal_state"),
				new OnOffBitItem(2, "Work_door_state"),
				new OnOffBitItem(3, "UC_state"),
				new OnOffBitItem(4, "Neutral_wire_contactor_state"),
				new OnOffBitItem(5, "On_off_grid_contactor_state") ) ) );
		wordRanges.add( new ModbusElementRange(0x0158, 
			new BitWordElement("Software_switch",
				new OnOffBitItem(0, "Inverter_communication_protection"),
				new OnOffBitItem(1, "IPM_temperature_protection"),
				new OnOffBitItem(2, "Power_reversal_protection"),
				new OnOffBitItem(3, "Low_voltage_ride_through_function") ),
			new BitWordElement("Software_switch",
				new OnOffBitItem(0, "Reconncet_following_abnormal_condition_disconnect_function"),
				new OnOffBitItem(1, "Remote_communication_protection") ) ) );
		wordRanges.add( new ModbusElementRange(0x180, 
			new BitWordElement("Abnormity",
				new OnOffBitItem(0, "DC_precharge_contactor_close_unsuccessfully"),
				new OnOffBitItem(1, "AC_precharge_contactor_close_unsuccessfully"),
				new OnOffBitItem(2, "AC_main_contactor_close_unsuccessfully"),
				new OnOffBitItem(3, "DC_electrical_breaker_1_close_unsuccessfully"),
				new OnOffBitItem(4, "DC_electrical_breaker_2_close_unsuccessfully"),
				new OnOffBitItem(5, "AC_breaker_trip"),
				new OnOffBitItem(6, "AC_main_breaker_open_when_running"),
				new OnOffBitItem(7, "DC_main_breaker_open_when_running"),
				new OnOffBitItem(8, "AC_main_breaker_open_unsuccessfully"),
				new OnOffBitItem(9, "DC_electrical_breaker_1_open_unsuccessfully"),
				new OnOffBitItem(10, "DC_electrical_breaker_2_open_unsuccessfully"),
				new OnOffBitItem(11, "Hardware_PDP_fault") ) ) );
		wordRanges.add( new ModbusElementRange(0x182,
			new BitWordElement("Abnormity",
				new OnOffBitItem(0, "DC_short_circuit_protection"),
				new OnOffBitItem(1, "DC_overvoltage_protection"),
				new OnOffBitItem(2, "DC_undervoltage_protection"),
				new OnOffBitItem(3, "DC_inverse_no_connection_protection"),
				new OnOffBitItem(4, "DC_disconnection_protection"),
				new OnOffBitItem(5, "Commuting_voltage_abnormity_protection"),
				new OnOffBitItem(6, "DC_overcurrent_protection"),
				new OnOffBitItem(7, "Phase_A_peak_current_over_limit_protection"),
				new OnOffBitItem(8, "Phase_B_peak_current_over_limit_protection"),
				new OnOffBitItem(9, "Phase_C_peak_current_over_limit_protection"),
				new OnOffBitItem(10, "Phase_A_virtual_current_over_limit_protection"),
				new OnOffBitItem(11, "Phase_B_virtual_current_over_limit_protection"),
				new OnOffBitItem(12, "Phase_C_virtual_current_over_limit_protection"),
				new OnOffBitItem(13, "Phase_A_grid_voltage_sampling_invalidation"),
				new OnOffBitItem(14, "Phase_B_grid_voltage_sampling_invalidation"),
				new OnOffBitItem(15, "Phase_C_grid_voltage_sampling_invalidation") ),
			new BitWordElement("Abnormity",
				new OnOffBitItem(0, "Phase_A_invert_voltage_sampling_invalidation"),
				new OnOffBitItem(1, "Phase_B_invert_voltage_sampling_invalidation"),
				new OnOffBitItem(2, "Phase_C_invert_voltage_sampling_invalidation"),
				new OnOffBitItem(3, "AC_current_sampling_invalidation"),
				new OnOffBitItem(4, "DC_current_sampling_invalidation"),
				new OnOffBitItem(5, "Phase_A_overtemperature_protection"),
				new OnOffBitItem(6, "Phase_B_overtemperature_protection"),
				new OnOffBitItem(7, "Phase_C_overtemperature_protection"),
				new OnOffBitItem(8, "Phase_A_temperature_sampling_invalidation"),
				new OnOffBitItem(9, "Phase_B_temperature_sampling_invalidation"),
				new OnOffBitItem(10, "Phase_C_temperature_sampling_invalidation"),
				new OnOffBitItem(11, "Phase_A_precharge_unmet_protection"),
				new OnOffBitItem(12, "Phase_B_precharge_unmet_protection"),
				new OnOffBitItem(13, "Phase_C_precharge_unmet_protection"),
				new OnOffBitItem(14, "Unadaptable_phase_sequence_error_protection"),
				new OnOffBitItem(15, "DSP_protection") ),
			new BitWordElement("Abnormity",
				new OnOffBitItem(0, "Phase_A_grid_voltage_severe_overvoltage_protection"),
				new OnOffBitItem(1, "Phase_A_grid_voltage_general_overvoltage_protection"),
				new OnOffBitItem(2, "Phase_B_grid_voltage_severe_overvoltage_protection"),
				new OnOffBitItem(3, "Phase_B_grid_voltage_general_overvoltage_protection"),
				new OnOffBitItem(4, "Phase_C_grid_voltage_severe_overvoltage_protection"),
				new OnOffBitItem(5, "Phase_C_grid_voltage_general_overvoltage_protection"),
				new OnOffBitItem(6, "Phase_A_grid_voltage_severe_undervoltage_protection"),
				new OnOffBitItem(7, "Phase_A_grid_voltage_general_undervoltage_protection"),
				new OnOffBitItem(8, "Phase_B_grid_voltage_severe_undervoltage_protection"),
				new OnOffBitItem(9, "Phase_B_grid_voltage_general_undervoltage_protection"),
				new OnOffBitItem(10, "Phase_C_grid_voltage_severe_undervoltage_protection"),
				new OnOffBitItem(11, "Phase_C_grid_voltage_general_undervoltage_protection"),
				new OnOffBitItem(12, "Severe_overfrequency_protection"),
				new OnOffBitItem(13, "General_overfrequency_protection"),
				new OnOffBitItem(14, "Severe_under_frequency_protection"),
				new OnOffBitItem(15, "General_under_frequency_protection") ),
			new BitWordElement("Abnormity",
				new OnOffBitItem(0, "Phase_A_grid_loss"),
				new OnOffBitItem(1, "Phase_B_grid_loss"),
				new OnOffBitItem(2, "Phase_C_grid_loss"),
				new OnOffBitItem(3, "Islanding_protection"),
				new OnOffBitItem(4, "Phase_A_under_voltage_ride_through"),
				new OnOffBitItem(5, "Phase_B_under_voltage_ride_through"),
				new OnOffBitItem(6, "Phase_C_under_voltage_ride_through") ),
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(0, "DC_precharge_contactor_inspection_abnormity"),
				new OnOffBitItem(1, "DC_breaker_1_inspection_abnormity"),
				new OnOffBitItem(2, "DC_breaker_2_inspection_abnormity"),
				new OnOffBitItem(3, "AC_precharge_contactor_inspection_abnormity"),
				new OnOffBitItem(4, "AC_main_contactor_inspection_abnormity"),
				new OnOffBitItem(5, "AC_breaker_inspection_abnormity"),
				new OnOffBitItem(6, "DC_breaker_1_close_unsuccessfully"),
				new OnOffBitItem(7, "DC_breaker_2_close_unsuccessfully"),
				new OnOffBitItem(8, "Control_signal_close_abnormally_inspected_by_system"),
				new OnOffBitItem(9, "Control_signal_open_abnormally_inspected_by_system"),
				new OnOffBitItem(10, "Neutral_wire_contactor_close_unsuccessfully"),
				new OnOffBitItem(11, "Neutral_wire_contactor_open_unsuccessfully"),
				new OnOffBitItem(12, "Work_door_open"),
				new OnOffBitItem(13, "Emergency_stop"),
				new OnOffBitItem(14, "AC_breaker_close_unsuccessfully"),
				new OnOffBitItem(15, "Control_switch_stop") ),
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(0, "General_overload"),
				new OnOffBitItem(1, "Severe_overload"),
				new OnOffBitItem(2, "Battery_current_over_limit"),
				new OnOffBitItem(3, "Power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(4, "Inverter_general_overtemperature"),
				new OnOffBitItem(5, "AC_three_phase_current_unbalance"),
				new OnOffBitItem(6, "Restore_factory_setting_unsuccessfully"),
				new OnOffBitItem(7, "Pole_board_invalidation"),
				new OnOffBitItem(8, "Self_inspection_failed"),
				new OnOffBitItem(9, "Receive_BMS_fault_and_stop"),
				new OnOffBitItem(10, "Refrigeration_equipment_invalidation"),
				new OnOffBitItem(11, "Large_temperature_difference_among_IGBT_three_phases"),
				new OnOffBitItem(12, "EEPROM_parameters_over_range"),
				new OnOffBitItem(13, "EEPROM_parameters_backup_failed") ),
			new BitWordElement("Suggestive_information",
				new OnOffBitItem(0, "Communication_between_inverter_and_BSMU_disconnected"),
				new OnOffBitItem(1, "Communication_between_inverter_and_Master_disconnected"),
				new OnOffBitItem(2, "Communication_between_inverter_and_UC_disconnected"),
				new OnOffBitItem(3, "BMS_start_overtime_controlled_by_PCS"),
				new OnOffBitItem(4, "BMS_stop_overtime_controlled_by_PCS"),
				new OnOffBitItem(5, "Sync_signal_invalidation"),
				new OnOffBitItem(6, "Sync_signal_continuous_caputure_fault"),
				new OnOffBitItem(7, "Sync_signal_several_times_caputure_fault") ) ) );
		wordRanges.add( new ModbusElementRange(0x0200,
			new DecimalWordItem("DC_Voltage"),
			new DecimalWordItem("DC_Current"),
			new DecimalWordItem("DC_Power") ) );
		wordRanges.add( new ModbusElementRange(0x0210,
			new DecimalWordItem("P"),
			new DecimalWordItem("Q"),
			new DecimalWordItem("S"),
			new DecimalWordItem("Ia"),
			new DecimalWordItem("Ib"),
			new DecimalWordItem("Ic") ) );
		wordRanges.add( new ModbusElementRange(0x0219,
			new DecimalWordItem("Ua"),
			new DecimalWordItem("Ub"),
			new DecimalWordItem("Uc"),
			new DecimalWordItem("Frequency") ) );
		wordRanges.add( new ModbusElementRange(0x0240,
			new DecimalWordItem("IPM_Phase_A_temperature"),
			new DecimalWordItem("IPM_Phase_B_temperature"),
			new DecimalWordItem("IPM_Phase_C_temperature") ) );
		wordRanges.add( new ModbusElementRange(0x0246,
			new DecimalWordItem("Environment_temperature") ) );
		wordRanges.add( new ModbusElementRange(0x0249,
			new BitWordElement("Transformer_Phase_B_temperature") ) );
		wordRanges.add( new ModbusElementRange(0x024F,
			new BitWordElement("Environment_humidity") ) );
		wordRanges.add( new ModbusElementRange(0x0500,
			new BitWordElement("Work_state_control_command",
				new OnOffBitItem(2, "Stop"),
				new OnOffBitItem(3, "Standby"),
				new OnOffBitItem(5, "Ready"),
				new OnOffBitItem(6, "Run") ) ) );
		wordRanges.add( new ModbusElementRange(0x0600, 
			new BitWordElement("Control_mode_control_command",
				new OnOffBitItem(0, "Remote"),
				new OnOffBitItem(1, "Local_Manual"),
				new OnOffBitItem(2, "Local_Auto") ),
			new BitWordElement("Work_mode_control_command",
				new OnOffBitItem(5, "Island") ),
			new DecimalWordItem("Clear_abnormity") ) );
		wordRanges.add( new ModbusElementRange(0x0635, 
			new DecimalWordItem("System_Min_SOC"),
			new DecimalWordItem("System_Max_SOC") ) );
		
		wordRanges.addAll( getPCSInformation("DCDC0", 0) );
		wordRanges.addAll( getPCSInformation("DCDC1", 0x300) );
		wordRanges.addAll( getPCSInformation("PV", 0x600) );
		
		// BMS
		wordRanges.add( new ModbusElementRange(0x1100, 
			new BitWordElement("Battery_string_1_work_state",
				new OnOffBitItem(0, "Initial"),
				new OnOffBitItem(1, "Stop"),
				new OnOffBitItem(2, "Starting_up"),
				new OnOffBitItem(3, "Running"),
				new OnOffBitItem(4, "Fault") ),
			new BitWordElement("Battery_string_1_switch_state",
				new OnOffBitItem(0, "Main_contactor"),
				new OnOffBitItem(1, "Precharge_contactor"),
				new OnOffBitItem(2, "FAN_contactor"),
				new OnOffBitItem(3, "BMU_power_supply_relay"),
				new OnOffBitItem(4, "Middle_relay") ),
			new BitWordElement("Battery_string_1_peripheral_IO_state",
				new OnOffBitItem(0, "Fuse_state"),
				new OnOffBitItem(4, "iIsolated_switch_state") ),
			new BitWordElement("Battery_string_1_suggestive_information",
				new OnOffBitItem(0, "Battery_string_1_charge_general_overcurrent"),
				new OnOffBitItem(1, "Battery_string_1_discharge_general_overcurrent"),
				new OnOffBitItem(2, "Battery_string_1_charge_current_over_limit"),
				new OnOffBitItem(3, "Battery_string_1_discharge_current_over_limit"),
				new OnOffBitItem(4, "Battery_string_1_general_overvoltage"),
				new OnOffBitItem(5, "Battery_string_1_general_undervoltage"),
				new OnOffBitItem(7, "Battery_string_1_general_over_temperature"),
				new OnOffBitItem(8, "Battery_string_1_general_under_temperature"),
				new OnOffBitItem(10, "Battery_string_1_severe_overvoltage"),
				new OnOffBitItem(11, "Battery_string_1_severe_under_voltage"),
				new OnOffBitItem(12, "Battery_string_1_severe_under_temperature"),
				new OnOffBitItem(13, "Battery_string_1_charge_severe_overcurrent"),
				new OnOffBitItem(14, "Battery_string_1_discharge_severe_overcurrent"),
				new OnOffBitItem(15, "Battery_string_1_capacity_abnormity") ) ) );
		wordRanges.add( new ModbusElementRange(0x1105, 
			new BitWordElement("Battery_string_1_abnormity",
				new OnOffBitItem(2, "Battery_string_1_voltage_sampling_route_invalidation"),
				new OnOffBitItem(4, "Battery_string_1_voltage_sampling_route_disconnected"),
				new OnOffBitItem(5, "Battery_string_1_temperature_sampling_route_disconnected"),
				new OnOffBitItem(6, "Battery_string_1_inside_CAN_disconnected"),
				new OnOffBitItem(9, "Battery_string_1_current_sampling_circuit_abnormity"),
				new OnOffBitItem(10, "Battery_string_1_battery_cell_invalidation"),
				new OnOffBitItem(11, "Battery_string_1_main_contactor_inspection_abnormity"),
				new OnOffBitItem(12, "Battery_string_1_precharge_contactor_inspection_abnormity"),
				new OnOffBitItem(13, "Battery_string_1_fan_inspection_abnormity"),
				new OnOffBitItem(14, "Battery_string_1_power_supply_relay_inspection_abnormity"),
				new OnOffBitItem(15, "Battery_string_1_middle_relay_abnormity") ),
			new BitWordElement("Battery_string_1_abnormity",
				new OnOffBitItem(2, "Battery_string_1_severe_overtemperature"),
				new OnOffBitItem(7, "Battery_string_1_smog_fault"),
				new OnOffBitItem(8, "Battery_string_1_blown_fuse_indicator_fault"),
				new OnOffBitItem(10, "Battery_string_1_general_leakage"),
				new OnOffBitItem(11, "Battery_string_1_severe_leakage"),
				new OnOffBitItem(12, "Communication_between_BECU_and_periphery_CAN_disconnected"),
				new OnOffBitItem(14, "Battery_string_1_power_supply_relay_contactor_disconnected") ) ) );
		wordRanges.add( new ModbusElementRange(0x1400, 
			new DecimalWordItem("Battery_string_1_total_voltage"),
			new DecimalWordItem("Battery_string_1_total_current"),
			new DecimalWordItem("Battery_string_1_SOC"),
			new DecimalWordItem("Battery_string_1_SOH"),
			new DecimalWordItem("Battery_string_1_cell_average_temperature") ) );
		wordRanges.add( new ModbusElementRange(0x1406, 
			new DecimalWordItem("Battery_string_1_charge_current_limit"),
			new DecimalWordItem("Battery_string_1_discharge_current_limit") ) );
		wordRanges.add( new ModbusElementRange(0x140A, 
			new DecimalWordItem("Battery_string_1_charge_discharge_times_LSW"),
			new DecimalWordItem("Battery_string_1_charge_discharge_times_MSW") ) );
		wordRanges.add( new ModbusElementRange(0x1418, 
			new DecimalWordItem("Battery_string_1_accumulated_charge_energy_LSW"),
			new DecimalWordItem("Battery_string_1_accumulated_charge_energy_MSW"),
			new DecimalWordItem("Battery_string_1_accumulated_discharge_energy_LSW"),
			new DecimalWordItem("Battery_string_1_accumulated_discharge_energy_MSW") ) );
		wordRanges.add( new ModbusElementRange(0x1430, 
			new DecimalWordItem("Battery_string_1_Max_cell_voltage_battery_NO"),
			new DecimalWordItem("Battery_string_1_Max_cell_voltage"),
			new DecimalWordItem("Temperature_of_battery_string_1_Max_voltage_cell"),
			new DecimalWordItem("Battery_string_1_Min_cell_voltage_battery_NO"),
			new DecimalWordItem("Battery_string_1_Min_cell_voltage"),
			new DecimalWordItem("Temperature_of_battery_string_1_Min_voltage_cell") ) );
		wordRanges.add( new ModbusElementRange(0x143A, 
			new DecimalWordItem("Battery_string_1_Max_cell_temperature_battery_NO"),
			new DecimalWordItem("Battery_string_1_Max_cell_temperature"),
			new DecimalWordItem("Voltage_of_battery_string_1_Max_temperature_cell"),
			new DecimalWordItem("Battery_string_1_Min_cell_temperature_battery_NO"),
			new DecimalWordItem("Battery_string_1_Min_cell_temperature"),
			new DecimalWordItem("Voltage_of_battery_string_1_Min_temperature_cell") ) );
		// 0x1500~0x16FF: Each address means one battery cell 
		wordRanges.add( new ModbusElementRange(0x1500, 
			new DecimalWordItem("Battery_string_1_cell_1_voltage"),
			new DecimalWordItem("Battery_string_1_cell_2_voltage") ) );
		// 0x1700~0x18FF: Each address means one battery cell
		wordRanges.add( new ModbusElementRange(0x1700, 
				new DecimalWordItem("Battery_string_1_cell_1_temperature"),
				new DecimalWordItem("Battery_string_1_cell_2_temperature") ) );
		
		return new CessProtocol(modbusinterface, unitid, wordRanges);
	}
	
	private static ArrayList<ModbusElementRange> getPCSInformation(String device, int offset) {
		ArrayList<ModbusElementRange> wordRanges = new ArrayList<ModbusElementRange>();
		wordRanges.add(	new ModbusElementRange(0xA000 + offset, 
			new BitWordElement(device + "_Work_state",
				new OnOffBitItem(1, "Self_checked"),
				new OnOffBitItem(2, "Idle"),
				new OnOffBitItem(3, "Ready"),
				new OnOffBitItem(4, "Running"),
				new OnOffBitItem(5, "Fault"),
				new OnOffBitItem(6, "Debug"),
				new OnOffBitItem(7, "Locked"),
				new OnOffBitItem(1, "Impulse") ),
			new BitWordElement(device + "_Work_state",
				new OnOffBitItem(4, "Const_current"),
				new OnOffBitItem(5, "Const_volt"),
				new OnOffBitItem(9, "Boost_MPPT") ) ) );
		wordRanges.add(	new ModbusElementRange(0xA100 + offset, 
			new BitWordElement(device + "_Suggestive_information",
				new OnOffBitItem(0, "Current_sampling_channel_abnormity_on_high_voltage_side"),
				new OnOffBitItem(1, "Current_sampling_channel_abnormity_on_low_voltage_side"),
				new OnOffBitItem(6, "EEPROM_parameters_over_range"),
				new OnOffBitItem(7, "Update_EEPROM_failed"),
				new OnOffBitItem(8, "Read_EEPROM_failed"),
				new OnOffBitItem(9, "Current_sampling_channel_abnormity_before_inductance") ),
			new BitWordElement(device + "_Suggestive_information",
				new OnOffBitItem(0, "Temperature_chanel1_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(1, "Temperature_chanel2_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(2, "Temperature_chanel3_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(3, "Temperature_chanel4_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(4, "Temperature_chanel5_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(5, "Temperature_chanel6_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(6, "Temperature_chanel7_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(7, "Temperature_chanel8_power_decrease_caused_by_overtemperature"),
				new OnOffBitItem(8, "Fan_1_stop_failed"),
				new OnOffBitItem(9, "Fan_2_stop_failed"),
				new OnOffBitItem(10, "Fan_3_stop_failed"),
				new OnOffBitItem(11, "Fan_4_stop_failed"),
				new OnOffBitItem(12, "Fan_1_startup_failed"),
				new OnOffBitItem(13, "Fan_2_startup_failed"),
				new OnOffBitItem(14, "Fan_3_startup_failed"),
				new OnOffBitItem(15, "Fan_4_startup_failed") ),
			new BitWordElement(device + "_Suggestive_information",
				new OnOffBitItem(0, "High_voltage_side_overvoltage"),
				new OnOffBitItem(1, "High_voltage_side_undervoltage"),
				new OnOffBitItem(2, "High_voltage_side_voltage_change_unconventionally") ),
			new BitWordElement(device + "_Suggestive_information",
				new OnOffBitItem(0, "Current_abnormity_before_DC_Converter_work_on_high_voltage_side"),
				new OnOffBitItem(1, "Current_abnormity_before_DC_Converter_work_on_low_voltage_side"),
				new OnOffBitItem(2, "Initial_Duty_Ratio_abnormity_before_DC_Converter_work"),
				new OnOffBitItem(3, "Voltage_abnormity_before_DC_Converter_work_on_high_voltage_side"),
				new OnOffBitItem(4, "Voltage_abnormity_before_DC_Converter_work_on_low_voltage_side") ),
			new BitWordElement(device + "_Suggestive_information",
				new OnOffBitItem(0, "High_voltage_breaker_inspection_abnormity"),
				new OnOffBitItem(1, "Low_voltage_breaker_inspection_abnormity"),
				new OnOffBitItem(2, "DC_precharge_contactor_inspection_abnormity"),
				new OnOffBitItem(3, "DC_precharge_contactor_open_unsuccessfully"),
				new OnOffBitItem(2, "DC_main_contactor_inspection_abnormity"),
				new OnOffBitItem(5, "DC_main_contactor_open_unsuccessfully"),
				new OnOffBitItem(6, "Output_contactor_close_unsuccessfully"),
				new OnOffBitItem(7, "Output_contactor_open_unsuccessfully"),
				new OnOffBitItem(8, "AC_main_contactor_close_unsuccessfully"),
				new OnOffBitItem(9, "AC_main_contactor_open_unsuccessfully") ) ) );
		wordRanges.add(	new ModbusElementRange(0xA110 + offset, 
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(0, "High_voltage_side_of_DC_Converter_undervoltage"),
				new OnOffBitItem(1, "High_voltage_side_of_DC_Converter_overvoltage"),
				new OnOffBitItem(2, "Low_voltage_side_of_DC_Converter_undervoltage"),
				new OnOffBitItem(3, "Low_voltage_side_of_DC_Converter_undervoltage"),
				new OnOffBitItem(4, "High_voltage_side_of_DC_Converter_overcurrent_fault"),
				new OnOffBitItem(5, "Low_voltage_side_of_DC_Converter_overcurrent"),
				new OnOffBitItem(6, "DC_Converter_IGBT_fault"),
				new OnOffBitItem(7, "DC_Converter_Precharge_unmet") ),
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(1, "DC_Converter_communication_disconnected"),
				new OnOffBitItem(2, "Current_configuration_over_range"),
				new OnOffBitItem(5, "Overcurrent_relay_fault"),
				new OnOffBitItem(6, "Lightning_protection_device_fault"),
				new OnOffBitItem(7, "DC_Converter_priamary_contactor_disconnected_abnormally"),
				new OnOffBitItem(9, "DC_disconnected_abnormally_on_low_voltage_side_of_DC_convetor"),
				new OnOffBitItem(12, "DC_convetor_EEPROM_abnormity_1"),
				new OnOffBitItem(13, "DC_converter_EEPROM_abnormity_1"),
				new OnOffBitItem(14, "EDC_converter_EEPROM_abnormity_1") ),
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(0, "DC_Convertor_general_overload"),
				new OnOffBitItem(1, "DC_short_circuit"),
				new OnOffBitItem(2, "Peak_pulse_current_protection"),
				new OnOffBitItem(3, "DC_disconnect_abnormally_on_high_voltage_side_of_DC_convetor"),
				new OnOffBitItem(4, "Effective_pulse_value_overhigh"),
				new OnOffBitItem(5, "DC_Converte_severe_overload"),
				new OnOffBitItem(6, "DC_breaker_disconnect_abnormally_on_high_voltage_side_of_DC_converter"),
				new OnOffBitItem(7, "DC_breaker_disconnect_abnormally_on_low_voltage_side_of_DC_converter"),
				new OnOffBitItem(8, "DC_converter_precharge_contactor_close_failed"),
				new OnOffBitItem(9, "DC_converter_main_contactor_close_failed"),
				new OnOffBitItem(10, "AC_contactor_state_abnormity_of_DC_convetor"),
				new OnOffBitItem(11, "DC_convetor_emergency_stop"),
				new OnOffBitItem(12, "DC_converter_charging_gun_disconnected"),
				new OnOffBitItem(13, "DC_current_abnormity_before_DC_convetor_work"),
				new OnOffBitItem(14, "Fuse_disconnected"),
				new OnOffBitItem(15, "DC_converter_hardware_current_or_voltage_fault") ),
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(0, "DC_converter_crystal_oscillator_circuit_invalidation"),
				new OnOffBitItem(1, "DC_converter_reset_circuit_invalidation"),
				new OnOffBitItem(2, "DC_converter_sampling_circuit_invalidation"),
				new OnOffBitItem(3, "DC_converter_digital_IO_circuit_invalidation"),
				new OnOffBitItem(4, "DC_converter_PWM_circuit_invalidation"),
				new OnOffBitItem(5, "DC_converter_X5045_circuit_invalidation"),
				new OnOffBitItem(6, "DC_converter_CAN_circuit_invalidation"),
				new OnOffBitItem(7, "DC_converter_software_hardware_protection_circuit_invalidation"),
				new OnOffBitItem(8, "DC_converter_power_circuit_invalidation"),
				new OnOffBitItem(9, "DC_converter_CPU_invalidation"),
				new OnOffBitItem(10, "DC_converter_TINT0_interrupt_invalidation"),
				new OnOffBitItem(11, "DC_converter_ADC_interrupt_invalidation"),
				new OnOffBitItem(12, "DC_converter_CAPITN4_interrupt_invalidation"),
				new OnOffBitItem(13, "DC_converter_CAPINT6_interrupt_invalidation"),
				new OnOffBitItem(14, "DC_converter_T3PINTinterrupt_invalidation"),
				new OnOffBitItem(15, "DC_converter_T4PINTinterrupt_invalidation") ),
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(0, "DC_converter_PDPINTA_interrupt_invalidation"),
				new OnOffBitItem(1, "DC_converter_T1PINT_interrupt_invalidation"),
				new OnOffBitItem(2, "DC_converter_RESV_interrupt_invalidation"),
				new OnOffBitItem(3, "DC_converter_100us_task_invalidation"),
				new OnOffBitItem(4, "DC_converter_clock_invalidation"),
				new OnOffBitItem(5, "DC_converter_EMS_memory_invalidation"),
				new OnOffBitItem(6, "DC_converter_exterior_communication_invalidation"),
				new OnOffBitItem(7, "DC_converter_exterior_communication_invalidation2"),
				new OnOffBitItem(8, "DC_converter_chanel_8_bound_fault"),
				new OnOffBitItem(9, "DC_converter_chanel_9_bound_fault"),
				new OnOffBitItem(10, "DC_converter_chanel_10_bound_fault"),
				new OnOffBitItem(11, "DC_converter_chanel_11_bound_fault"),
				new OnOffBitItem(12, "DC_converter_chanel_12_bound_fault"),
				new OnOffBitItem(13, "DC_converter_chanel_13_bound_fault") ),
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(0, "DC_Converter_chanel_1_over_temperature"),
				new OnOffBitItem(1, "DC_Converter_chanel_2_over_temperature"),
				new OnOffBitItem(2, "DC_Converter_chanel_3_over_temperature"),
				new OnOffBitItem(3, "DC_Converter_chanel_4_over_temperature"),
				new OnOffBitItem(4, "DC_Converter_chanel_5_over_temperature"),
				new OnOffBitItem(5, "DC_Converter_chanel_6_over_temperature"),
				new OnOffBitItem(6, "DC_Converter_chanel_7_over_temperature"),
				new OnOffBitItem(7, "DC_Converter_chanel_8_over_temperature"),
				new OnOffBitItem(8, "DC_Converter_chanel_1_temperature_sampling_invalidation"),
				new OnOffBitItem(9, "DC_Converter_chanel_2_temperature_sampling_invalidation"),
				new OnOffBitItem(10, "DC_Converter_chanel_3_temperature_sampling_invalidation"),
				new OnOffBitItem(11, "DC_Converter_chanel_4_temperature_sampling_invalidation"),
				new OnOffBitItem(12, "DC_Converter_chanel_5_temperature_sampling_invalidation"),
				new OnOffBitItem(13, "DC_Converter_chanel_6_temperature_sampling_invalidation"),
				new OnOffBitItem(14, "DC_Converter_chanel_7_temperature_sampling_invalidation"),
				new OnOffBitItem(15, "DC_Converter_chanel_8_temperature_sampling_invalidation") ),
			new BitWordElement(device + "_Abnormity",
				new OnOffBitItem(4, "DC_Converter_inductance_current_sampling_invalidation"),
				new OnOffBitItem(5, "Current_sampling_invalidation_on_the_low_voltage_sideof_DC_Converter"),
				new OnOffBitItem(6, "Voltage_sampling_invalidation_on_the_low_voltage_side_of_DC_Converter"),
				new OnOffBitItem(7, "Insulation_inspection_fault") ) ) );
		wordRanges.add(	new ModbusElementRange(0xA120,
			new BitWordElement(device + "_Switch_state",
				new OnOffBitItem(0, "DC_precharge_contactor"),
				new OnOffBitItem(1, "DC_main_contactor"),
				new OnOffBitItem(2, "Output_contactor"),
				new OnOffBitItem(3, "Output_breaker"),
				new OnOffBitItem(4, "Input_breaker"),
				new OnOffBitItem(5, "AC_contactor"),
				new OnOffBitItem(6, "Emergency_stop_button") ) ) );
		wordRanges.add(	new ModbusElementRange(0xA130,
			new DecimalWordItem(device + "_Output_voltage"),
			new DecimalWordItem(device + "_Output_current"),
			new DecimalWordItem(device + "_Output_power"),
			new DecimalWordItem(device + "_Input_voltage"),
			new DecimalWordItem(device + "_Input_current"),
			new DecimalWordItem(device + "_Input_power") ) );
		wordRanges.add(	new ModbusElementRange(0xA140,
			new DecimalWordItem(device + "_Reactor_temperature"),
			new DecimalWordItem(device + "_IGBT_temperature") ) );
		wordRanges.add(	new ModbusElementRange(0xA200,
			new DecimalWordItem(device + "_current_limit_under_constant_voltage_mode"),
			new DecimalWordItem(device + "_high_voltage_limit_under_constant_current_mode"),
			new DecimalWordItem(device + "_low_voltage_limit_under_constant_current_mode") ) );
		return wordRanges;
	}
}