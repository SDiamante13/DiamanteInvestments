package tech.pathtoprogramming.diamanteinvestments;

import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;

// Alpha Vantage API Key: NKNKJCBRLYI9H5SO

public class StockChartData {
	
	private final Deque<Date> dates;
	private Deque<Double> opens;
	private Deque<Double> highs;
	private Deque<Double> lows;
	private Deque<Double> closes;
	private Deque<Double> volumes;
	private TimeFrame timeSeries;
	private String symbol;
	private Interval interval;
	Date date;


	public StockChartData(TimeFrame timeSeries,
						  String symbol,
						  Interval interval,
						  Deque<Date> dates,
						  Deque<Double> opens,
						  Deque<Double> highs,
						  Deque<Double> lows,
						  Deque<Double> closes,
						  Deque<Double> volumes) {
		this.timeSeries = timeSeries;
		this.symbol = symbol;
		this.interval = interval;
		this.dates = dates;
		this.opens = opens;
		this.highs = highs;
		this.lows = lows;
		this.closes = closes;
		this.volumes = volumes;
	}

	// constructor
	public StockChartData(TimeFrame timeSeries, String symbol, Interval interval)
	{
		this.timeSeries = timeSeries;
		this.symbol = symbol;
		this.interval = interval;
		
		dates = new ArrayDeque<>();
		opens = new ArrayDeque<>();
		highs = new ArrayDeque<>();
		lows = new ArrayDeque<>();
		closes = new ArrayDeque<>();
		volumes = new ArrayDeque<>();
		int target = 0;
		int year = 0, month = 0, day = 1, hour = 1, min = 1, sec = 1;
		int start = 0;
		String tempPrice = "0";
		
		
		String url = "https://www.alphavantage.co/query?function=" + this.timeSeries
				+ "&symbol=" + this.symbol
				+ "&interval=" + this.interval
				+ "&apikey=NKNKJCBRLYI9H5SO&datatype=csv";
		
		// Alpha Advantage returns a csv file with dates, opens, highs, lows, closes, volumes
		
		try {
			URL alphaAdvantage = new URL(url);
			URLConnection data = alphaAdvantage.openConnection();
			Scanner input = new Scanner(data.getInputStream());
			if(input.hasNext()) { // skip header line
				input.nextLine();
			}
			// read in data
			while(input.hasNextLine()) {
				NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
				String line = input.nextLine();
				// push dates
				target = line.indexOf('-');
				// Grab year
				year = Integer.valueOf(line.substring(0, target));
				// Grab month
				start = target++ + 1;
				target = line.indexOf('-', target);
				month = Integer.valueOf(line.substring(start, target));
				if(timeSeries != TimeFrame.INTRADAY) {
				// Grab day for timeseries other than INTRADAY
				start = target++ + 1;
				target = line.indexOf(',', target);
				day = Integer.valueOf(line.substring(start, target));
				}
				else {
					// Grab day for INTRADAY
					start = target++ + 1;
					target = line.indexOf(' ', target);
					day = Integer.valueOf(line.substring(start, target));
				}
				// Grab hours, min, & sec for INTRADAY
				if(timeSeries == TimeFrame.INTRADAY) {
					start = target++ + 1;
					target = line.indexOf(':', target);
					hour = Integer.valueOf(line.substring(start, target));
					start = target++ + 1;
					target = line.indexOf(':', target);
					min = Integer.valueOf(line.substring(start, target));
					start = target++ + 1;
					target = line.indexOf(',', target);
					sec = Integer.valueOf(line.substring(start, target));
					date = new Date(year, month, day, hour, min, sec);
				}
				else {
					date = new Date(year, month, day);
				}
				//2018-02-16 00:00:00,
				dates.push(date);
				
			
				// push open prices
				start = target++ + 1;
				target = line.indexOf(",", target);
				// The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
				tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
				tempPrice = tempPrice.substring(1,tempPrice.length()); // trim off $
				opens.push(Double.parseDouble(tempPrice));	
			
				// push high prices
				start = target++ + 1;
				target = line.indexOf(",", target);
				// The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
				tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
				tempPrice = tempPrice.substring(1,tempPrice.length()); // trim off $
				highs.push(Double.parseDouble(tempPrice));	
			
				// push low prices
				start = target++ + 1;
				target = line.indexOf(",", target);
				// The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
				tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
				tempPrice = tempPrice.substring(1,tempPrice.length()); // trim off $
				lows.push(Double.parseDouble(tempPrice));
			
				// push close prices
				start = target++ + 1;
				target = line.indexOf(",", target);
				// The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
				tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
				tempPrice = tempPrice.substring(1,tempPrice.length()); // trim off $
				closes.push(Double.parseDouble(tempPrice));	
			
				// push volumes
				start = target++ + 1;
				target = line.length()-1;
				volumes.push(Double.parseDouble(line.substring(start, target)));	
			}
				
			input.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	public Deque<Date> getDates() {
		return dates;
	}

	public Deque<Double> getOpens() {
		return opens;
	}

	public Deque<Double> getHighs() {
		return highs;
	}

	public Deque<Double> getLows() {
		return lows;
	}

	public Deque<Double> getCloses() {
		return closes;
	}

	public Deque<Double> getVolumes() {
		return volumes;
	}
	
	public TimeFrame getTimeFrame() {
		return timeSeries;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public Interval getInterval() {
		return interval;
	}
	
}

