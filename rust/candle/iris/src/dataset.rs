use candle_core::{Device, IndexOp, Tensor};
use csv::Reader;
use rand::{rng, seq::SliceRandom};

#[derive(Debug, Clone)]
pub struct Dataset {
    pub data: Tensor,
    pub labels: Tensor,
    pub size: usize,
}

type DatasetError = Box<dyn std::error::Error>;

impl Dataset {
    pub fn load(path: &str, device: &Device) -> Result<Self, DatasetError> {
        let mut reader = Reader::from_path(path)?;

        let mut data: Vec<f32> = vec![];
        let mut labels: Vec<u32> = vec![];

        for r in reader.records() {
            let rec = r?;

            for i in 0..4 {
                data.push(to_f32(rec.get(i))?);
            }

            labels.push(to_factor_iris(&rec.get(4)));
        }

        let size = labels.len();

        Ok(Self {
            data: Tensor::from_vec(data, (size, 4), device)?,
            labels: Tensor::from_vec(labels, size, device)?,
            size,
        })
    }

    pub fn split_train_test(
        &self,
        test_rate: f32,
        device: &Device,
    ) -> Result<(Self, Self), DatasetError> {
        if test_rate < 1.0 {
            let test_size = (self.size as f32 * test_rate) as usize;
            let train_size = self.size - test_size;

            let mut idx = (0..self.size as u32).collect::<Vec<_>>();
            idx.shuffle(&mut rng());

            let idx = Tensor::from_vec(idx, self.size, device)?;

            let train_idx = idx.i(..train_size)?;
            let test_idx = idx.i(train_size..)?;

            let train_ds = Self {
                data: self.data.index_select(&train_idx, 0)?,
                labels: self.labels.index_select(&train_idx, 0)?,
                size: train_size,
            };

            let test_ds = Self {
                data: self.data.index_select(&test_idx, 0)?,
                labels: self.labels.index_select(&test_idx, 0)?,
                size: test_size,
            };

            Ok((train_ds, test_ds))
        } else {
            Err("must be test_size < 1.0".into())
        }
    }
}

fn to_f32(s: Option<&str>) -> Result<f32, DatasetError> {
    if let Some(s) = s {
        s.parse().map_err(|e| format!("{}", e).into())
    } else {
        Err("none".into())
    }
}

fn to_factor_iris(label: &Option<&str>) -> u32 {
    match label {
        Some("setosa") => 0,
        Some("versicolor") => 1,
        _ => 2,
    }
}
